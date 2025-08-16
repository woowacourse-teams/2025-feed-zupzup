package feedzupzup.backend.qr.domain;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SiteUrl {

    private final String baseUrl;
    private final Map<String, String> queryParams;

    public SiteUrl(@Value("${page.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.queryParams = new LinkedHashMap<>();
    }

    public SiteUrl builder() {
        final SiteUrl copied = new SiteUrl(this.baseUrl);
        copied.queryParams.putAll(this.queryParams);
        return copied;
    }

    public SiteUrl addParam(final String key, final String value) {
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
