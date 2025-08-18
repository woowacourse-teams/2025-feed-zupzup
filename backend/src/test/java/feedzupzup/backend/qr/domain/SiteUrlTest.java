package feedzupzup.backend.qr.domain;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.qr.config.QRConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {QRConfiguration.class, SiteUrl.class})
class SiteUrlTest {

    private static final String BASE_URL = "https://test.feedzupzup.com";

    @Autowired
    private SiteUrl siteUrl;

    @Test
    @DisplayName("파라미터 없는 기본 URL을 반환한다")
    void build_with_no_params() {
        // when
        final String result = siteUrl.builder().build();

        // then
        assertThat(result).isEqualTo(BASE_URL);
    }

    @Test
    @DisplayName("여러 쿼리 파라미터를 추가하고 특수문자를 URL 인코딩한다")
    void build_with_multiple_params_and_encoding() {
        // given
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
    @DisplayName("builder()를 여러 번 호출해도 독립적인 인스턴스를 생성한다")
    void builder_creates_independent_instances() {
        // when
        final String result1 = siteUrl.builder().addParam("param1", "value1").build();
        final String result2 = siteUrl.builder().addParam("param2", "value2").build();
        final String originalResult = siteUrl.builder().build();

        // then
        assertThat(result1).isEqualTo(BASE_URL + "?param1=value1");
        assertThat(result2).isEqualTo(BASE_URL + "?param2=value2");
        assertThat(originalResult).isEqualTo(BASE_URL);
    }
}
