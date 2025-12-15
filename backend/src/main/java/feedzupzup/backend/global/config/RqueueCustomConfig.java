package feedzupzup.backend.global.config;

import com.github.sonus21.rqueue.config.SimpleRqueueListenerContainerFactory;
import feedzupzup.backend.feedback.application.retry.RandomExponentialBackOff;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RqueueCustomConfig {

    @Bean
    public SimpleRqueueListenerContainerFactory simpleRqueueListenerContainerFactory() {
        SimpleRqueueListenerContainerFactory factory = new SimpleRqueueListenerContainerFactory();
        factory.setTaskExecutionBackOff(new RandomExponentialBackOff());
        return factory;
    }
}