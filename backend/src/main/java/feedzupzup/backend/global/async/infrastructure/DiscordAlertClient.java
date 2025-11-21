package feedzupzup.backend.global.async.infrastructure;

import feedzupzup.backend.global.async.AsyncFailureAlertService;
import feedzupzup.backend.global.async.AsyncTaskFailure;
import feedzupzup.backend.global.async.AsyncTaskFailureRepository;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class DiscordAlertClient implements AsyncFailureAlertService {
    //현재 디스코드 알람을 비동기 작업 실패 밖에 안써서 직접 구현하게 했으나, 추후 디스코드 알람을 다른 곳에서 쓴다면 분리해야할 필요성이 있음.
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${app.discord.webhook.enabled:false}")
    private boolean webhookEnabled;

    private final AsyncTaskFailureRepository asyncTaskFailureRepository;
    private final RestClient discordClient;

    @Async("externalApiExecutor")
    @Override
    public void alert(final Long asyncTaskFailureId) {
        if (!webhookEnabled) {
            log.debug("Discord 웹훅이 비활성화되었거나 URL이 설정되지 않음");
            return;
        }
        AsyncTaskFailure asyncTaskFailure = asyncTaskFailureRepository.findById(asyncTaskFailureId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 실패작업 ID(id:" + asyncTaskFailureId + ")로 찾을 수 없습니다."));

        try {
            String message = createFailureMessage(asyncTaskFailure);
            discordClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("content", message))
                    .retrieve()
                    .toBodilessEntity();

            log.info("Discord 알림 전송 완료: 작업={}, 대상ID={}",
                    asyncTaskFailure.getTaskType(), asyncTaskFailure.getTargetId());

        } catch (Exception e) {
            log.error("Discord 알림 전송 실패: 작업={}, 대상ID={}",
                    asyncTaskFailure.getTaskType(), asyncTaskFailure.getTargetId(), e);
            throw e;
        }
    }

    private String createFailureMessage(final AsyncTaskFailure failure) {
        return String.format(
                ":warning: **비동기 작업 최종 실패** :warning:\n\n" +
                        "**작업 종류**: %s\n" +
                        "**대상 ID**: %s\n" +
                        "**재시도 횟수**: %d\n" +
                        "**실패 시간**: %s\n" +
                        "**에러 메시지**: ```%s```",
                failure.getTaskType().getDescription(),
                failure.getTargetId(),
                failure.getRetryCount(),
                failure.getCreatedAt().format(FORMATTER),
                failure.getErrorMessage().length() > 500 ?
                        failure.getErrorMessage().substring(0, 500) + "..." : failure.getErrorMessage()
        );
    }
}
