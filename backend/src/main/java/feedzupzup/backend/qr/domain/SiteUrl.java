package feedzupzup.backend.qr.domain;

import feedzupzup.backend.qr.config.QRProperties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SiteUrl {

    private final QRProperties qrProperties;

    public SiteUrlBuilder builder() {
        return new SiteUrlBuilder(qrProperties.baseUrl());
    }

    public static class SiteUrlBuilder {
        private final String baseUrl;
        private final Map<String, String> queryParams;

        private SiteUrlBuilder(final String baseUrl) {
            this.baseUrl = baseUrl;
            this.queryParams = new LinkedHashMap<>();
        }

        public SiteUrlBuilder addParam(final String key, final String value) {
            this.queryParams.put(key, value);
            return this;
        }

        public String build() {
            if (queryParams.isEmpty()) {
                return baseUrl;
            }

            final String queryString = queryParams.entrySet().stream()
                    .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) +
                            "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            return baseUrl + "?" + queryString;
        }
    }
}
