package feedzupzup.backend.feedback.infrastructure;

import com.vane.badwordfiltering.BadWordFiltering;
import feedzupzup.backend.feedback.domain.service.moderation.ContentFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AbuseContentFilter implements ContentFilter {

    private static final String[] SUBSTITUTION_WORDS = {"멍멍", "야옹", "음메"};
    private static final String ASTERISK_PLACEHOLDER = "\u0000";
    private static final BadWordFiltering FILTER = new BadWordFiltering();

    @Override
    public String filter(final String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        String processedText = text.replace("*", ASTERISK_PLACEHOLDER);

        /*
         * BadWordFiltering 라이브러리는 HashSet을 상속받아 욕설 단어들을 저장하고 관리합니다.
         * - check(): HashSet을 순회하며 욕설 포함 여부 검사
         * - change(): HashSet의 욕설들을 substituteValue 필드 값으로 치환
         *
         * 스레드 안전하지 않아 동시 접근 시 ConcurrentModificationException 발생 가능
         * Spring은 멀티 스레드이므로 synchronized로 한 번에 한 스레드만 FILTER 접근하도록 보장
         */
        synchronized (FILTER) {
            if (!FILTER.check(processedText)) {
                return processedText.replace(ASTERISK_PLACEHOLDER, "*");
            }

            String result = FILTER.change(processedText);
            int index = 0;
            while (result.contains("*")) {
                result = result.replaceFirst("\\*+", SUBSTITUTION_WORDS[index++ % SUBSTITUTION_WORDS.length]);
            }
            String filteredResult = result.replace(ASTERISK_PLACEHOLDER, "*");

            log.info("욕설 필터링 수행 - Before: [{}], After: [{}]", text, filteredResult);

            return filteredResult;
        }
    }
}
