package lcy.jwt.application;

import lcy.jwt.domain.User;
import lcy.jwt.domain.UserRepository;
import lcy.jwt.domain.UserRole;
import lcy.jwt.dto.RegisterUserRequest;
import lcy.jwt.dto.RegisterUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public RegisterUserResponse register(RegisterUserRequest request) {
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
        return RegisterUserResponse.of(user.getId(), request);
    }

}
