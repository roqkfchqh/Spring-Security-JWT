package lcy.jwt.ui.api;

import jakarta.validation.Valid;
import lcy.jwt.ui.docs.AuthControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import lcy.jwt.application.AuthService;

import lcy.jwt.dto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {
    private final AuthService authService;

    @Override
    @PostMapping("/register/admin")
    public ResponseEntity<UserResponse> registerAdmin(
            @RequestBody @Valid RegisterAdminRequest request
    ) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @RequestBody @Valid RegisterUserRequest request
    ) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(
            @RequestBody @Valid LoginUserRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
}
