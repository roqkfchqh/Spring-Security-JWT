package lcy.jwt.mocks;

import lcy.jwt.domain.User;
import lcy.jwt.domain.UserRole;

public class MockUserFactory {
    public static User createAdmin(Long id) {
        User admin = User.of(
                "adminuser",
                "Admin User",
                "admin123",
                UserRole.ADMIN
        );
        admin.setId(id);
        return admin;
    }

    public static User createUser(Long id) {
        User user = User.of(
                "testuser",
                "Test User",
                "password123",
                UserRole.USER
        );
        user.setId(id);
        return user;
    }
}
