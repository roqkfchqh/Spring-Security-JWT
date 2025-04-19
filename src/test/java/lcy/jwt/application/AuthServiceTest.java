package lcy.jwt.application;

import lcy.jwt.domain.User;
import lcy.jwt.domain.UserRepository;
import lcy.jwt.domain.UserRole;
import lcy.jwt.dto.LoginUserResponse;
import lcy.jwt.dto.RegisterAdminRequest;
import lcy.jwt.dto.RegisterUserRequest;
import lcy.jwt.dto.LoginUserRequest;
import lcy.jwt.dto.UserResponse;
import lcy.jwt.exception.CustomException;
import lcy.jwt.mocks.MockRequestFactory;
import lcy.jwt.mocks.MockUserFactory;
import lcy.jwt.security.JwtUtil;
import lcy.jwt.utils.JwtProperties;
import lcy.jwt.utils.SecretCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private SecretCode secretCode;
    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerAdmin_성공() {
        RegisterAdminRequest req = MockRequestFactory.registerAdmin();
        given(userRepository.existsByUsername(req.username())).willReturn(false);
        given(secretCode.code()).willReturn("1234");

        UserResponse res = authService.registerAdmin(req);

        assertEquals(UserRole.ADMIN.toString(), res.role());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerAdmin_관리자_코드_불일치_예외() {
        given(secretCode.code()).willReturn("wrong");
        RegisterAdminRequest req = MockRequestFactory.registerAdmin();

        CustomException ex = assertThrows(CustomException.class,
                () -> authService.registerAdmin(req));
        assertEquals("ad403", ex.getCode());
    }

    @Test
    void registerAdmin_이미_존재하는_사용자_예외() {
        given(userRepository.existsByUsername("adminuser")).willReturn(true);
        RegisterAdminRequest req = MockRequestFactory.registerAdmin();
        given(secretCode.code()).willReturn("1234");

        CustomException ex = assertThrows(CustomException.class,
                () -> authService.registerAdmin(req));
        assertEquals("au400", ex.getCode());
    }

    @Test
    void registerUser_성공() {
        RegisterUserRequest req = MockRequestFactory.registerUser();
        given(userRepository.existsByUsername(req.username())).willReturn(false);

        UserResponse res = authService.registerUser(req);

        assertEquals(UserRole.USER.toString(), res.role());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_이미_존재하는_사용자_예외() {
        given(userRepository.existsByUsername("testuser")).willReturn(true);
        RegisterUserRequest req = MockRequestFactory.registerUser();

        CustomException ex = assertThrows(CustomException.class,
                () -> authService.registerUser(req));
        assertEquals("au400", ex.getCode());
    }

    @Test
    void login_성공() {
        LoginUserRequest req = MockRequestFactory.loginUser();
        User user = MockUserFactory.createUser(5L);
        given(userRepository.findByUsername(req.username())).willReturn(Optional.of(user));
        given(encoder.matches(req.password(), user.getPassword())).willReturn(true);
        given(jwtUtil.createToken(5L, UserRole.USER)).willReturn("jwt-token");
        given(jwtProperties.token()).willReturn(new JwtProperties.Token("prefix", 3600L));

        LoginUserResponse res = authService.login(req);

        assertEquals("jwt-token", res.token());
    }

    @Test
    void login_존재하지_않는_사용자_예외() {
        given(userRepository.findByUsername("testuser")).willReturn(Optional.empty());
        LoginUserRequest req = MockRequestFactory.loginUser();

        CustomException ex = assertThrows(CustomException.class,
                () -> authService.login(req));
        assertEquals("us404", ex.getCode());
    }

    @Test
    void login_비밀번호_틀릴때_예외() {
        User user = MockUserFactory.createUser(6L);
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(encoder.matches("password123", user.getPassword())).willReturn(false);
        LoginUserRequest req = MockRequestFactory.loginUser();

        CustomException ex = assertThrows(CustomException.class,
                () -> authService.login(req));
        assertEquals("au401", ex.getCode());
    }
}
