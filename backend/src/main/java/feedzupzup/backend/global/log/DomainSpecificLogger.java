package feedzupzup.backend.global.log;

import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackSecretResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DomainSpecificLogger {

    @AfterReturning(
            pointcut = "execution(* feedzupzup.backend.feedback.domain.FeedBackRepository.save(..))",
            returning = "feedback"
    )
    public void createFeedbackLog(final Feedback feedback) {
        log.info("successfully saved feedback : " + "feedbackId = " + feedback.getId());
    }

    @AfterReturning(
            "execution(* feedzupzup.backend.feedback.domain.FeedBackRepository.deleteById(..))"
    )
    public void deleteFeedbackLog(final JoinPoint joinPoint) {
        final Object[] args = joinPoint.getArgs();
        final Long feedbackId = (Long) args[0];
        log.info("successfully delete feedback : " + "removed feedbackId = " + feedbackId);
    }

    @AfterReturning(
            pointcut = "execution(* feedzupzup.backend.feedback.application.AdminFeedbackService.updateFeedbackStatus(..))",
            returning = "updateFeedbackStatusResponse"
    )
    public void updateFeedbackStatusLog(
            final UpdateFeedbackStatusResponse updateFeedbackStatusResponse
    ) {
        log.info("successfully update feedback status : " +
                "feedbackId = " + updateFeedbackStatusResponse.feedbackId() + ", "
                + "status = " + updateFeedbackStatusResponse.status());
    }

    @AfterReturning(
            pointcut = "execution(* feedzupzup.backend.feedback.application.AdminFeedbackService.updateFeedbackSecret(..))",
            returning = "updateFeedbackSecretResponse"
    )
    public void updateFeedbackSecretLog(
            final UpdateFeedbackSecretResponse updateFeedbackSecretResponse
    ) {
        log.info("successfully update feedback's secret status :" +
                "feedbackId = " + updateFeedbackSecretResponse.feedbackId() + ", "
                + "isSecret = " + updateFeedbackSecretResponse.isSecret());
    }
}
