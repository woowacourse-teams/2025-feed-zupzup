package feedzupzup.backend.global.log;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = BusinessActionLogIntegrationTest.TestConfig.class)
@ContextConfiguration
class BusinessActionLogIntegrationTest {

    @Autowired
    private TestService testService;

    private ListAppender<ILoggingEvent> logAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(BusinessActionLogger.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
        logger.setLevel(Level.INFO);
    }

    @Test
    @DisplayName("@BusinessActionLog 어노테이션이 적용된 메서드의 로그가 올바르게 출력된다")
    void domainLogAnnotationWorksCorrectly() {
        // when
        final String result = testService.testMethod("input", 123L);

        // then
        assertThat(result).isEqualTo("processed");

        final List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);

        final ILoggingEvent logEvent = logEvents.get(0);
        assertThat(logEvent.getLevel()).isEqualTo(Level.INFO);
        assertThat(logEvent.getFormattedMessage())
                .contains("[SUCCESS]")
                .contains("duration=")
                .contains("ms")
                .contains("location=TestService.testMethod()")
                .contains("method=testMethod")
                .contains("params={input=(String) input, number=(Long) 123}")
                .contains("response=(String) processed");
    }

    @Test
    @DisplayName("@BusinessActionLog 어노테이션이 없는 메서드는 로그가 출력되지 않는다")
    void methodWithoutDomainLogAnnotationDoesNotLog() {
        // when
        final String result = testService.methodWithoutLog("input");

        // then
        assertThat(result).isEqualTo("no log");

        final List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).isEmpty();
    }

    @Test
    @DisplayName("void 메서드의 로그가 올바르게 출력된다")
    void voidMethodLogsCorrectly() {
        // when
        testService.voidMethod(456L);

        // then
        final List<ILoggingEvent> logEvents = logAppender.list;
        assertThat(logEvents).hasSize(1);

        final ILoggingEvent logEvent = logEvents.get(0);
        assertThat(logEvent.getLevel()).isEqualTo(Level.INFO);
        assertThat(logEvent.getFormattedMessage())
                .contains("[SUCCESS]")
                .contains("duration=")
                .contains("ms")
                .contains("location=TestService.voidMethod()")
                .contains("method=voidMethod")
                .contains("params={id=(Long) 456}")
                .contains("response=null");
    }

    @Configuration
    @EnableAspectJAutoProxy
    static class TestConfig {

        @Bean
        public BusinessActionLogger domainSpecificLogger() {
            return new BusinessActionLogger();
        }

        @Bean
        public TestService testService() {
            return new TestService();
        }
    }

    static class TestService {

        @BusinessActionLog
        public String testMethod(final String input, final Long number) {
            return "processed";
        }

        public String methodWithoutLog(final String input) {
            return "no log";
        }

        @BusinessActionLog
        public void voidMethod(final Long id) {
            // void method for testing
        }
    }
}
