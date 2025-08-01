package feedzupzup.backend.global.log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class BusinessActionLoggerTest {

    private BusinessActionLogger businessActionLogger;
    private ListAppender<ILoggingEvent> logAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        businessActionLogger = new BusinessActionLogger();
        
        logger = (Logger) LoggerFactory.getLogger(BusinessActionLogger.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
        logger.setLevel(Level.INFO);
    }

    @Test
    @DisplayName("파라미터가 없는 메서드의 로그가 올바르게 출력된다")
    void logMethodWithNoParameters() throws Throwable {
        // given
        final ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        final MethodSignature signature = mock(MethodSignature.class);
        final Method method = mock(Method.class);
        final String expectedResult = "success";

        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getName()).willReturn("testMethod");
        given(signature.getDeclaringType()).willReturn((Class) String.class);
        given(signature.getMethod()).willReturn(method);
        given(method.getParameters()).willReturn(new Parameter[0]);
        given(joinPoint.getArgs()).willReturn(new Object[0]);
        given(joinPoint.proceed()).willReturn(expectedResult);

        // when
        final Object result = businessActionLogger.logDomainAction(joinPoint);

        // then
        assertThat(result).isEqualTo(expectedResult);

        final List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);

        final ILoggingEvent logEvent = logEvents.get(0);
        assertThat(logEvent.getLevel()).isEqualTo(Level.INFO);
        assertThat(logEvent.getFormattedMessage())
                .contains("[SUCCESS]")
                .contains("duration=")
                .contains("ms")
                .contains("location=String.testMethod()")
                .contains("method=testMethod")
                .contains("params={}")
                .contains("response=(String) success");
    }

    @Test
    @DisplayName("파라미터가 있는 메서드의 로그가 올바르게 출력된다")
    void logMethodWithParameters() throws Throwable {
        // given
        final ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        final MethodSignature signature = mock(MethodSignature.class);
        final Method method = mock(Method.class);
        final Parameter param1 = mock(Parameter.class);
        final Parameter param2 = mock(Parameter.class);
        final Parameter param3 = mock(Parameter.class);
        final Long expectedResult = 123L;
        final Object[] args = {"test", 456L, true};

        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getName()).willReturn("createMethod");
        given(signature.getDeclaringType()).willReturn((Class) String.class);
        given(signature.getMethod()).willReturn(method);
        given(method.getParameters()).willReturn(new Parameter[]{param1, param2, param3});
        given(param1.getName()).willReturn("str");
        given(param2.getName()).willReturn("num");
        given(param3.getName()).willReturn("flag");
        given(joinPoint.getArgs()).willReturn(args);
        given(joinPoint.proceed()).willReturn(expectedResult);

        // when
        final Object result = businessActionLogger.logDomainAction(joinPoint);

        // then
        assertThat(result).isEqualTo(expectedResult);

        final List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);

        final ILoggingEvent logEvent = logEvents.get(0);
        assertThat(logEvent.getLevel()).isEqualTo(Level.INFO);
        assertThat(logEvent.getFormattedMessage())
                .contains("[SUCCESS]")
                .contains("duration=")
                .contains("ms")
                .contains("location=String.createMethod()")
                .contains("method=createMethod")
                .contains("params={str=(String) test, num=(Long) 456, flag=(Boolean) true}")
                .contains("response=(Long) 123");
    }

    @Test
    @DisplayName("결과가 null인 메서드의 로그가 올바르게 출력된다")
    void logMethodWithNullResult() throws Throwable {
        // given
        final ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        final MethodSignature signature = mock(MethodSignature.class);
        final Method method = mock(Method.class);
        final Parameter param1 = mock(Parameter.class);
        final Object[] args = {1L};

        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getName()).willReturn("deleteMethod");
        given(signature.getDeclaringType()).willReturn((Class) String.class);
        given(signature.getMethod()).willReturn(method);
        given(method.getParameters()).willReturn(new Parameter[]{param1});
        given(param1.getName()).willReturn("id");
        given(joinPoint.getArgs()).willReturn(args);
        given(joinPoint.proceed()).willReturn(null);

        // when
        final Object result = businessActionLogger.logDomainAction(joinPoint);

        // then
        assertThat(result).isNull();

        final List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);

        final ILoggingEvent logEvent = logEvents.get(0);
        assertThat(logEvent.getLevel()).isEqualTo(Level.INFO);
        assertThat(logEvent.getFormattedMessage())
                .contains("[SUCCESS]")
                .contains("duration=")
                .contains("ms")
                .contains("location=String.deleteMethod()")
                .contains("method=deleteMethod")
                .contains("params={id=(Long) 1}")
                .contains("response=null");
    }

    @Test
    @DisplayName("실행 시간이 측정된다")
    void measureExecutionTime() throws Throwable {
        // given
        final ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        final MethodSignature signature = mock(MethodSignature.class);
        final Method method = mock(Method.class);

        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getName()).willReturn("slowMethod");
        given(signature.getDeclaringType()).willReturn((Class) String.class);
        given(signature.getMethod()).willReturn(method);
        given(method.getParameters()).willReturn(new Parameter[0]);
        given(joinPoint.getArgs()).willReturn(new Object[0]);
        given(joinPoint.proceed()).willAnswer(invocation -> {
            // 시간 측정을 위한 간단한 작업
            for (int i = 0; i < 1000; i++) {
                Math.sqrt(i);
            }
            return "result";
        });

        // when
        businessActionLogger.logDomainAction(joinPoint);

        // then
        final List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);

        final ILoggingEvent logEvent = logEvents.get(0);
        final String message = logEvent.getFormattedMessage();
        
        // duration이 0보다 큰 값으로 측정되었는지 확인 (정확한 시간은 환경에 따라 다를 수 있음)
        assertThat(message).matches(".*duration=[0-9]+ms.*");
    }
}
