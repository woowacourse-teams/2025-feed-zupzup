package feedzupzup.backend.auth.presentation.interceptor;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.GUEST_ID;
import static feedzupzup.backend.global.util.CookieUtilization.GUEST_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.google.common.net.HttpHeaders;
import feedzupzup.backend.auth.presentation.annotation.SavedGuest;
import feedzupzup.backend.auth.presentation.annotation.VisitedGuest;
import feedzupzup.backend.global.util.CookieUtilization;
import feedzupzup.backend.guest.application.GuestService;
import feedzupzup.backend.guest.domain.guest.GuestActiveTracker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseCookie;
import org.springframework.web.method.HandlerMethod;

@ExtendWith(MockitoExtension.class)
class GuestInterceptorTest {

    @Mock
    private GuestService guestService;

    @Mock
    private CookieUtilization cookieUtilization;

    @Mock
    private GuestActiveTracker guestActiveTracker;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HandlerMethod handlerMethod;

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private ResponseCookie responseCookie;

    @InjectMocks
    private GuestInterceptor guestInterceptor;

    @Test
    @DisplayName("Handler가 HandlerMethod가 아니면 true를 반환한다")
    void preHandle_NotHandlerMethod_ReturnsTrue() throws Exception {
        // given
        Object handler = new Object();

        // when
        boolean result = guestInterceptor.preHandle(request, response, handler);

        // then
        assertThat(result).isTrue();
        verify(cookieUtilization, never()).getGuestIdFromCookie(any());
    }

    @Test
    @DisplayName("Guest 어노테이션이 없으면 true를 반환한다")
    void preHandle_NoGuestAnnotation_ReturnsTrue() throws Exception {
        // given
        MethodParameter[] parameters = new MethodParameter[0];
        given(handlerMethod.getMethodParameters()).willReturn(parameters);

        // when
        boolean result = guestInterceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(result).isTrue();
        verify(cookieUtilization, never()).getGuestIdFromCookie(any());
    }

    @Nested
    @DisplayName("쿠키에 guestId가 없는 경우")
    class NoCookieTests {

        @Test
        @DisplayName("VisitedGuest 어노테이션이 있고 쿠키가 없으면 새 UUID를 생성하고 저장하지 않는다")
        void preHandle_VisitedGuestAnnotation_NoCookie_CreatesNewIdWithoutSaving() throws Exception {
            // given
            given(methodParameter.hasParameterAnnotation(SavedGuest.class)).willReturn(false);
            given(methodParameter.hasParameterAnnotation(VisitedGuest.class)).willReturn(true);
            given(handlerMethod.getMethodParameters()).willReturn(new MethodParameter[]{methodParameter});
            given(cookieUtilization.getGuestIdFromCookie(request)).willReturn(Optional.empty());
            given(cookieUtilization.createCookie(eq(GUEST_KEY), any(UUID.class))).willReturn(responseCookie);
            given(responseCookie.toString()).willReturn("cookie-string");

            // when
            boolean result = guestInterceptor.preHandle(request, response, handlerMethod);

            // then
            assertThat(result).isTrue();
            verify(guestService, never()).save(any(UUID.class));
            verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), eq("cookie-string"));
            verify(request).setAttribute(eq(GUEST_ID.getValue()), any(UUID.class));
        }

        @Test
        @DisplayName("SavedGuest 어노테이션이 있고 쿠키가 없으면 새 UUID를 생성하고 저장한다")
        void preHandle_SavedGuestAnnotation_NoCookie_CreatesNewIdAndSaves() throws Exception {
            // given
            given(methodParameter.hasParameterAnnotation(SavedGuest.class)).willReturn(true);
            given(handlerMethod.getMethodParameters()).willReturn(new MethodParameter[]{methodParameter});
            given(cookieUtilization.getGuestIdFromCookie(request)).willReturn(Optional.empty());
            given(cookieUtilization.createCookie(eq(GUEST_KEY), any(UUID.class))).willReturn(responseCookie);
            given(responseCookie.toString()).willReturn("cookie-string");

            // when
            boolean result = guestInterceptor.preHandle(request, response, handlerMethod);

            // then
            assertThat(result).isTrue();
            verify(guestService).save(any(UUID.class));
            verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), eq("cookie-string"));
            verify(request).setAttribute(eq(GUEST_ID.getValue()), any(UUID.class));
        }
    }

    @Nested
    @DisplayName("쿠키에 guestId가 있는 경우")
    class WithCookieTests {

        @Test
        @DisplayName("VisitedGuest 어노테이션이 있으면 활동을 추적하고 저장하지 않는다")
        void preHandle_VisitedGuestAnnotation_WithCookie_TracksActivityWithoutSaving() throws Exception {
            // given
            UUID guestId = UUID.randomUUID();
            given(methodParameter.hasParameterAnnotation(SavedGuest.class)).willReturn(false);
            given(methodParameter.hasParameterAnnotation(VisitedGuest.class)).willReturn(true);
            given(handlerMethod.getMethodParameters()).willReturn(new MethodParameter[]{methodParameter});
            given(cookieUtilization.getGuestIdFromCookie(request)).willReturn(Optional.of(guestId));
            given(cookieUtilization.createCookie(GUEST_KEY, guestId)).willReturn(responseCookie);
            given(responseCookie.toString()).willReturn("cookie-string");

            // when
            boolean result = guestInterceptor.preHandle(request, response, handlerMethod);

            // then
            assertThat(result).isTrue();
            verify(guestActiveTracker).trackActivity(guestId);
            verify(guestService, never()).save(any(UUID.class));
            verify(response).addHeader(HttpHeaders.SET_COOKIE, "cookie-string");
            verify(request).setAttribute(GUEST_ID.getValue(), guestId);
        }

        @Test
        @DisplayName("SavedGuest 어노테이션이 있고 저장되지 않은 Guest면 저장한다")
        void preHandle_SavedGuestAnnotation_NotSavedGuest_SavesGuest() throws Exception {
            // given
            UUID guestId = UUID.randomUUID();
            given(methodParameter.hasParameterAnnotation(SavedGuest.class)).willReturn(true);
            given(handlerMethod.getMethodParameters()).willReturn(new MethodParameter[]{methodParameter});
            given(cookieUtilization.getGuestIdFromCookie(request)).willReturn(Optional.of(guestId));
            given(guestService.isSavedGuest(guestId)).willReturn(false);
            given(cookieUtilization.createCookie(GUEST_KEY, guestId)).willReturn(responseCookie);
            given(responseCookie.toString()).willReturn("cookie-string");

            // when
            boolean result = guestInterceptor.preHandle(request, response, handlerMethod);

            // then
            assertThat(result).isTrue();
            verify(guestService).isSavedGuest(guestId);
            verify(guestService).save(guestId);
            verify(guestActiveTracker).trackActivity(guestId);
            verify(response).addHeader(HttpHeaders.SET_COOKIE, "cookie-string");
            verify(request).setAttribute(GUEST_ID.getValue(), guestId);
        }

        @Test
        @DisplayName("SavedGuest 어노테이션이 있고 이미 저장된 Guest면 저장하지 않는다")
        void preHandle_SavedGuestAnnotation_AlreadySavedGuest_DoesNotSave() throws Exception {
            // given
            UUID guestId = UUID.randomUUID();
            given(methodParameter.hasParameterAnnotation(SavedGuest.class)).willReturn(true);
            given(handlerMethod.getMethodParameters()).willReturn(new MethodParameter[]{methodParameter});
            given(cookieUtilization.getGuestIdFromCookie(request)).willReturn(Optional.of(guestId));
            given(guestService.isSavedGuest(guestId)).willReturn(true);
            given(cookieUtilization.createCookie(GUEST_KEY, guestId)).willReturn(responseCookie);
            given(responseCookie.toString()).willReturn("cookie-string");

            // when
            boolean result = guestInterceptor.preHandle(request, response, handlerMethod);

            // then
            assertThat(result).isTrue();
            verify(guestService).isSavedGuest(guestId);
            verify(guestService, never()).save(guestId);
            verify(guestActiveTracker).trackActivity(guestId);
            verify(response).addHeader(HttpHeaders.SET_COOKIE, "cookie-string");
            verify(request).setAttribute(GUEST_ID.getValue(), guestId);
        }
    }
}
