package lcy.jwt.mocks;

import lcy.jwt.dto.LoginUserRequest;
import lcy.jwt.dto.RegisterAdminRequest;
import lcy.jwt.dto.RegisterUserRequest;

public class MockRequestFactory {
    public static RegisterAdminRequest registerAdmin() {
        return new RegisterAdminRequest(
                "adminuser",
                "Admin User",
                "admin123",
                "1234"
        );
    }

    public static RegisterUserRequest registerUser() {
        return new RegisterUserRequest(
                "testuser",
                "Test User",
                "password123"
        );
    }

    public static LoginUserRequest loginUser() {
        return new LoginUserRequest(
                "testuser",
                "password123"
        );
    }
}
