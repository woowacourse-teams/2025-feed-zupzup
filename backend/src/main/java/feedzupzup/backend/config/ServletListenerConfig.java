package feedzupzup.backend.config;

import feedzupzup.backend.auth.listener.AdminSessionListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletListenerConfig {

    @Bean
    public ServletListenerRegistrationBean<AdminSessionListener> adminSessionListener(
            final AdminSessionListener listener) {
        return new ServletListenerRegistrationBean<>(listener);
    }
}