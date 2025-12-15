package feedzupzup.backend.s3.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.global.exception.GlobalExceptionHandler;
import feedzupzup.backend.s3.dto.request.PresignedUrlRequest;
import feedzupzup.backend.s3.dto.response.PresignedUrlResponse;
import feedzupzup.backend.s3.service.S3PresignedUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class S3ControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private S3PresignedUploadService s3PresignedUploadService;

    @InjectMocks
    private S3Controller s3Controller;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(s3Controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("유효한 요청으로 presigned URL을 성공적으로 발급받는다")
    void generate_presigned_url_success() throws Exception {
        // given
        PresignedUrlRequest request = new PresignedUrlRequest("feedbacks", "jpg");
        String presignedUrl = "https://s3.amazonaws.com/bucket/feedbacks/test-uuid.jpg?signature=xxx";
        PresignedUrlResponse response = new PresignedUrlResponse(presignedUrl, "image/jpeg");

        given(s3PresignedUploadService.requestPresignedUrl(eq("feedbacks"), eq("jpg")))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.presignedUrl").value(presignedUrl))
                .andExpect(jsonPath("$.data.contentType").value("image/jpeg"));

        verify(s3PresignedUploadService).requestPresignedUrl(eq("feedbacks"), eq("jpg"));
    }

    @Test
    @DisplayName("PNG 확장자로 presigned URL을 발급받는다")
    void generate_presigned_url_with_png_extension() throws Exception {
        // given
        PresignedUrlRequest request = new PresignedUrlRequest("profiles", "png");
        String presignedUrl = "https://s3.amazonaws.com/bucket/profiles/test-uuid.png?signature=xxx";
        PresignedUrlResponse response = new PresignedUrlResponse(presignedUrl, "image/png");

        given(s3PresignedUploadService.requestPresignedUrl(eq("profiles"), eq("png")))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.presignedUrl").value(presignedUrl))
                .andExpect(jsonPath("$.data.contentType").value("image/png"));

        verify(s3PresignedUploadService).requestPresignedUrl(eq("profiles"), eq("png"));
    }

    @Test
    @DisplayName("JPEG 확장자로 presigned URL을 발급받는다")
    void generate_presigned_url_with_jpeg_extension() throws Exception {
        // given
        PresignedUrlRequest request = new PresignedUrlRequest("images", "jpeg");
        String presignedUrl = "https://s3.amazonaws.com/bucket/images/test-uuid.jpeg?signature=xxx";
        PresignedUrlResponse response = new PresignedUrlResponse(presignedUrl, "image/jpeg");

        given(s3PresignedUploadService.requestPresignedUrl(eq("images"), eq("jpeg")))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.presignedUrl").value(presignedUrl))
                .andExpect(jsonPath("$.data.contentType").value("image/jpeg"));

        verify(s3PresignedUploadService).requestPresignedUrl(eq("images"), eq("jpeg"));
    }

    @Test
    @DisplayName("다른 objectDir로 presigned URL을 발급받는다")
    void generate_presigned_url_with_different_object_dir() throws Exception {
        // given
        PresignedUrlRequest request = new PresignedUrlRequest("attachments", "jpg");
        String presignedUrl = "https://s3.amazonaws.com/bucket/attachments/test-uuid.jpg?signature=xxx";
        PresignedUrlResponse response = new PresignedUrlResponse(presignedUrl, "image/jpeg");

        given(s3PresignedUploadService.requestPresignedUrl(eq("attachments"), eq("jpg")))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.presignedUrl").value(presignedUrl))
                .andExpect(jsonPath("$.data.contentType").value("image/jpeg"));

        verify(s3PresignedUploadService).requestPresignedUrl(eq("attachments"), eq("jpg"));
    }
}