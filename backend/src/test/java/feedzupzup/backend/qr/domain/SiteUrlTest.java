package feedzupzup.backend.qr.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SiteUrlTest {

    private static final String BASE_URL = "https://feedzupzup.com";

    @Test
    @DisplayName("파라미터 없는 기본 URL을 반환한다")
    void build_with_no_params() {
        // given
        final SiteUrl siteUrl = new SiteUrl(BASE_URL);

        // when
        final String result = siteUrl.builder().build();

        // then
        assertThat(result).isEqualTo(BASE_URL);
    }

    @Test
    @DisplayName("여러 쿼리 파라미터를 추가하고 특수문자를 URL 인코딩한다")
    void build_with_multiple_params_and_encoding() {
        // given
        final SiteUrl siteUrl = new SiteUrl(BASE_URL);
        final String uuid = "123e4567-e89b-12d3-a456-426614174000";
        final String specialValue = "안녕하세요 world!";

        // when
        final String result = siteUrl.builder()
                .addParam("uuid", uuid)
                .addParam("message", specialValue)
                .build();

        // then
        assertThat(result).isEqualTo(
                BASE_URL + "?uuid=" + uuid + "&message=%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94+world%21");
    }

    @Test
    @DisplayName("builder()를 여러 번 호출해도 원본 객체에 영향을 주지 않는다")
    void builder_creates_independent_instances() {
        // given
        final SiteUrl originalSiteUrl = new SiteUrl(BASE_URL);

        // when
        final SiteUrl builder1 = originalSiteUrl.builder().addParam("param1", "value1");
        final SiteUrl builder2 = originalSiteUrl.builder().addParam("param2", "value2");

        final String result1 = builder1.build();
        final String result2 = builder2.build();
        final String originalResult = originalSiteUrl.build();

        // then
        assertThat(result1).isEqualTo(BASE_URL + "?param1=value1");
        assertThat(result2).isEqualTo(BASE_URL + "?param2=value2");
        assertThat(originalResult).isEqualTo(BASE_URL);
    }
}
