package feedzupzup.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://192.168.7.246:3000",
                        "http://192.168.7.174:3000",
                        "http://192.168.6.6:3000",
                        "http://192.168.7.214:3000",
                        "https://feedzupzup.com"
                )
                .allowedOriginPatterns(
                        "https://*.feedzupzup.com",
                        "http://*.feedzupzup.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
