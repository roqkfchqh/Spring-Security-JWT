package lcy.jwt.api;

import lcy.jwt.application.AdminService;
import lcy.jwt.config.MockAuthUser;
import lcy.jwt.domain.User;
import lcy.jwt.dto.UserResponse;
import lcy.jwt.domain.UserRole;
import lcy.jwt.exception.ErrorResponseHandler;
import lcy.jwt.mocks.MockUserFactory;
import lcy.jwt.security.*;
import lcy.jwt.ui.api.AdminController;
import lcy.jwt.utils.JwtProperties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@EnableConfigurationProperties(JwtProperties.class)
@Import({
        SecurityConfig.class,
        JwtUtil.class,
        JwtAuthenticationFilter.class,
        ErrorResponseHandler.class,
        CustomAuthEntryPoint.class,
        CustomAccessDeniedHandler.class,
})
class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AdminService adminService;

    @Test
    @MockAuthUser(userId = 1L, role = UserRole.ADMIN)
    void 권한이_ADMIN일때_200() throws Exception {
        long targetUserId = 2L;
        User targetUser = MockUserFactory.createUser(targetUserId);
        given(adminService.assignAdminRole(targetUserId))
                .willReturn(UserResponse.of(targetUser));

        mockMvc.perform(patch("/api/admin/users/{userId}/roles", targetUserId))
                .andExpect(status().isOk());
    }

    @Test
    @MockAuthUser(userId = 2L, role = UserRole.USER)
    void 권한이_USER일때_403() throws Exception {
        mockMvc.perform(patch("/api/admin/users/{userId}/roles", 42L))
                .andExpect(status().isForbidden());
    }

    @Test
    void 비로그인유저일떄_401() throws Exception {
        mockMvc.perform(patch("/api/admin/users/{userId}/roles", 42L))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AdminService adminService() {
            return Mockito.mock(AdminService.class);
        }
    }
}
