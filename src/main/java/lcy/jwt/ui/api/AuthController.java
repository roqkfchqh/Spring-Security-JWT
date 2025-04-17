package lcy.jwt.ui.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lcy.jwt.application.AuthService;
import lcy.jwt.dto.LoginUserRequest;
import lcy.jwt.dto.LoginUserResponse;
import lcy.jwt.dto.RegisterUserRequest;
import lcy.jwt.dto.RegisterUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증/인가 관련 API")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "USER 회원가입",
            description = "username 중복 비허용, size 1-10<br/>nickname 중복 허용, size 1-10<br/>password size 8-20"
    )
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(
            @RequestBody @Valid RegisterUserRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "USER 로그인"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(
            @RequestBody @Valid LoginUserRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
}
