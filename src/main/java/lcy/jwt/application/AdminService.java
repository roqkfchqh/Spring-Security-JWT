package lcy.jwt.application;

import lcy.jwt.domain.User;
import lcy.jwt.domain.UserRepository;
import lcy.jwt.domain.UserRole;
import lcy.jwt.dto.UserResponse;
import lcy.jwt.exception.CustomException;
import lcy.jwt.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    public UserResponse assignAdminRole(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (user.getRole().equals(UserRole.ADMIN)) {
            throw new CustomException(ErrorCode.ALREADY_ADMIN);
        }
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);
        return UserResponse.of(user);
    }
}
