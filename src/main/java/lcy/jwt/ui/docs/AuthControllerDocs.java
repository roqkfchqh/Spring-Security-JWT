package lcy.jwt.ui.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lcy.jwt.dto.*;

@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증/인가 관련 API")
public interface AuthControllerDocs {

    @Operation(summary = "ADMIN 회원가입", description = "Secret code 입력 필수 (현재 1234)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name  = "가입 성공",
                                    value = """
                                            {
                                              "id": 1,
                                              "username": "admin1",
                                              "nickname": "관리자",
                                              "role": "ADMIN"
                                            }"""
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패: ge400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name  = "유효성 검사 실패",
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "code": "ge400",
                                              "message": "유효성 검사에 실패했습니다."
                                            }"""
                            )
                    )
            ),
            @ApiResponse(responseCode = "403", description = "관리자 코드 입력오류: ad403",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name  = "관리자 코드 오류",
                                    value = """
                                            {
                                              "status": 403,
                                              "error": "Forbidden",
                                              "code": "ad403",
                                              "message": "관리자 코드가 잘못되었습니다."
                                            }"""
                            )
                    )
            )
    })
    @PostMapping("/register/admin")
    ResponseEntity<UserResponse> registerAdmin(@RequestBody @Valid RegisterAdminRequest request);

    @Operation(summary = "USER 회원가입", description = "username/nickname/password 유효성 검사 적용")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name  = "가입 성공",
                                    value = """
                                            {
                                              "id": 2,
                                              "username": "user1",
                                              "nickname": "유저",
                                              "role": "USER"
                                            }"""
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패 또는 중복 username",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name  = "유효성 검사 실패",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "code": "ge400",
                                                      "message": "유효성 검사에 실패했습니다."
                                                    }"""
                                    ),
                                    @ExampleObject(
                                            name  = "중복 username",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "code": "au400",
                                                      "message": "이미 사용중인 username 입니다."
                                                    }"""
                                    )
                            }
                    )
            )
    })
    @PostMapping("/register")
    ResponseEntity<UserResponse> registerUser(@RequestBody @Valid RegisterUserRequest request);

    @Operation(summary = "로그인", description = "아이디·비밀번호로 로그인하여 JWT 토큰 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name  = "로그인 성공",
                                    value = """
                                            {
                                              "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                            }"""
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "패스워드 오류: au401",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name  = "패스워드 오류",
                                    value = """
                                            {
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "code": "au401",
                                              "message": "패스워드가 일치하지 않습니다."
                                            }"""
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음: us404",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name  = "유저 없음",
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "code": "us404",
                                              "message": "존재하지 않는 사용자입니다."
                                            }"""
                            )
                    )
            )
    })
    @PostMapping("/login")
    ResponseEntity<LoginUserResponse> login(@RequestBody @Valid LoginUserRequest request);
}
