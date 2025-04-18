package lcy.jwt.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lcy.jwt.application.AuthService;
import lcy.jwt.exception.ErrorResponseHandler;
import lcy.jwt.security.*;
import lcy.jwt.ui.api.AuthController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({
        JwtUtil.class,
        JwtAuthenticationFilter.class,
        SecurityConfig.class,
        CustomAuthEntryPoint.class,
        CustomAccessDeniedHandler.class,
        ErrorResponseHandler.class
})
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void registerAdmin_잘못된_요청_400() throws Exception {
        Map<String, Object> invalid = new HashMap<>();
        invalid.put("username", "veeeeerrrrryyyllllloooooooonnnnnnnnnggggggggggg");
        invalid.put("nickname", "veeeeerrrrryyyllllloooooooonnnnnnnnnggggggggggg");
        invalid.put("password", "short");
        invalid.put("secretCode", "1234");

        mockMvc.perform(post("/api/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_잘못된_요청_400() throws Exception {
        Map<String, Object> invalid = new HashMap<>();
        invalid.put("username", "veeeeerrrrryyyllllloooooooonnnnnnnnnggggggggggg");
        invalid.put("nickname", "veeeeerrrrryyyllllloooooooonnnnnnnnnggggggggggg");
        invalid.put("password", "short");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
    }
}
