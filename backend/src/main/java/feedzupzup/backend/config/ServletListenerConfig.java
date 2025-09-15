package feedzupzup.backend.config;

import feedzupzup.backend.auth.application.ActiveSessionStore;
import feedzupzup.backend.auth.listener.AdminSessionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletListenerConfig {

    @Bean
    public AdminSessionListener adminSessionListener(
            final ActiveSessionStore activeSessionStore,
            @Value("${admin.session.key}") final String adminIdSessionKey) {
        return new AdminSessionListener(activeSessionStore, adminIdSessionKey);
    }

    @Bean
    public ServletListenerRegistrationBean<AdminSessionListener> adminSessionListenerRegistration(
            final AdminSessionListener listener) {
        return new ServletListenerRegistrationBean<>(listener);
    }
}