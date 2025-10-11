package feedzupzup.backend.global.config;

import feedzupzup.backend.auth.presentation.interceptor.AdminCheckInterceptor;
import feedzupzup.backend.auth.presentation.interceptor.GuestInterceptor;
import feedzupzup.backend.auth.presentation.resolver.AdminOrganizationArgumentResolver;
import feedzupzup.backend.auth.presentation.resolver.AdminSessionArgumentResolver;
import feedzupzup.backend.auth.presentation.resolver.GuestArgumentResolver;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final GuestArgumentResolver guestArgumentResolver;
    private final AdminSessionArgumentResolver adminSessionArgumentResolver;
    private final AdminOrganizationArgumentResolver adminOrganizationArgumentResolver;
    private final AdminCheckInterceptor adminCheckInterceptor;
    private final GuestInterceptor guestInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(guestArgumentResolver);
        resolvers.add(adminSessionArgumentResolver);
        resolvers.add(adminOrganizationArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminCheckInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login", "/admin/sign-up");

        registry.addInterceptor(guestInterceptor)
                .excludePathPatterns("/admin/**");
    }
}
