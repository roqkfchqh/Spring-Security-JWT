package lcy.jwt.ui.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lcy.jwt.application.AuthService;
import lcy.jwt.dto.*;
import lcy.jwt.exception.ErrorResponse;
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

    @Operation(summary = "ADMIN 회원가입", description = "Secret code 입력 필수 (현재 1234)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가입 성공"
            ),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패: ge400, 중복 username: au400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "관리자 code 입력오류: ad403",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
    })
    @PostMapping("/register/admin")
    public ResponseEntity<UserResponse> register(
            @RequestBody @Valid RegisterAdminRequest request
    ) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }

    @Operation(summary = "USER 회원가입", description = "username/nickname/password 유효성 검사 적용")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가입 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패: ge400, 중복 username: au400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody @Valid RegisterUserRequest request
    ) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @Operation(summary = "로그인", description = "아이디·비밀번호로 로그인하여 JWT 토큰 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginUserResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "패스워드 오류: au401",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "해당 User 가 존재하지 않는 경우: us404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
    })
    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(
            @RequestBody @Valid LoginUserRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
}
