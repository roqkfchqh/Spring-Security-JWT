package lcy.jwt.application;

import lcy.jwt.domain.User;
import lcy.jwt.domain.UserRepository;
import lcy.jwt.domain.UserRole;
import lcy.jwt.dto.*;
import lcy.jwt.exception.CustomException;
import lcy.jwt.exception.ErrorCode;
import lcy.jwt.security.JwtUtil;
import lcy.jwt.utils.JwtProperties;
import lcy.jwt.utils.JwtTokenUtils;
import lcy.jwt.utils.SecretCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final SecretCode secretCode;

    public UserResponse registerAdmin(RegisterAdminRequest request) {
        if (!request.secretCode().equals(secretCode.code())) {
            throw new CustomException(ErrorCode.ADMIN_CODE_FORBIDDEN);
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_USED);
        }
        User user = User.of(
                request.username(),
                request.nickname(),
                encoder.encode(request.password()),
                UserRole.ADMIN
        );
        userRepository.save(user);
        return UserResponse.of(user);
    }

    public UserResponse registerUser(RegisterUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_USED);
        }
        User user = User.of(
                request.username(),
                request.nickname(),
                encoder.encode(request.password()),
                UserRole.USER
        );
        userRepository.save(user);
        return UserResponse.of(user);
    }

    public LoginUserResponse login(LoginUserRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }
        String token = jwtUtil.createToken(user.getId(), user.getRole());
        String rawToken = JwtTokenUtils.removePrefix(token, jwtProperties.token().prefix());
        return LoginUserResponse.of(rawToken);
    }
}
