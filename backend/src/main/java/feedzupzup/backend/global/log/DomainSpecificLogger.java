package feedzupzup.backend.global.log;

import feedzupzup.backend.feedback.domain.Feedback;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DomainSpecificLogger {

    @Around("execution(* feedzupzup.backend.feedback.domain.FeedBackRepository.save(..))")
    public Object logSave(ProceedingJoinPoint joinPoint) throws Throwable{
        final Object result = joinPoint.proceed();
        final Feedback feedback = (Feedback) result;
        log.info("successfully saved feedback : " + "feedbackId = " + feedback.getId());
        return result;
    }
}
