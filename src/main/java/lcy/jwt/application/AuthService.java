package lcy.jwt.application;

import jakarta.validation.Valid;
import lcy.jwt.domain.User;
import lcy.jwt.domain.UserRepository;
import lcy.jwt.domain.UserRole;
import lcy.jwt.dto.*;
import lcy.jwt.security.JwtUtil;
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
    private final SecretCode secretCode;

    public UserResponse register(@Valid RegisterAdminRequest request) {
        if (!request.secretCode().equals(secretCode.code())){
            throw new IllegalArgumentException("관리자 code 가 잘못되었습니다.");
        }
        if (userRepository.existsByUsername(request.username())){
            throw new IllegalArgumentException("이미 사용중인 username 입니다.");
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

    public UserResponse register(RegisterUserRequest request) {
        if (userRepository.existsByUsername(request.username())){
            throw new IllegalArgumentException("이미 사용중인 username 입니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        if (!encoder.matches(request.password(), user.getPassword())){
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }
        String token = jwtUtil.createToken(user.getId(), user.getRole());
        return LoginUserResponse.of(token);
    }
}
