package lcy.jwt.application;

import lcy.jwt.domain.User;
import lcy.jwt.domain.UserRepository;
import lcy.jwt.domain.UserRole;
import lcy.jwt.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    public UserResponse assignAdminRole(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        if (user.getRole().equals(UserRole.ADMIN)) {
            throw new IllegalArgumentException("해당 유저는 이미 관리자입니다.");
        }
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);
        return UserResponse.of(user);
    }
}
