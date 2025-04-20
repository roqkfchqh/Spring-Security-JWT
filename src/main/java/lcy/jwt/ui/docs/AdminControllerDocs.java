package lcy.jwt.ui.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lcy.jwt.dto.UserResponse;
import lcy.jwt.domain.UserRole;

@Tag(name = "Admin", description = "관리자 관련 API")
@RequestMapping("/api/admin")
@Secured(UserRole.Authority.ADMIN)
@ApiResponse(
        responseCode = "401",
        description  = "로그인 필요: se401",
        content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                        name  = "로그인 필요",
                        value = """
                                {
                                  "status": 401,
                                  "error": "Unauthorized",
                                  "code": "se401",
                                  "message": "로그인이 필요합니다."
                                }"""
                )
        )
)
@ApiResponse(
        responseCode = "403",
        description  = "ADMIN 권한 없음: se403",
        content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                        name  = "권한 없음",
                        value = """
                                {
                                  "status": 403,
                                  "error": "Forbidden",
                                  "code": "se403",
                                  "message": "접근 권한이 없습니다."
                                }"""
                )
        )
)
public interface AdminControllerDocs {

    @Operation(
            summary     = "관리자 권한 부여",
            description = "지정한 유저 ID에 ADMIN 권한을 부여합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "권한 부여 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name  = "성공",
                                    value = """
                                            {
                                              "id": 5,
                                              "username": "user5",
                                              "nickname": "유저5",
                                              "role": "ADMIN"
                                            }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description  = "이미 ADMIN 인 경우: ad400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name  = "이미 ADMIN",
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "code": "ad400",
                                              "message": "해당 유저는 이미 관리자입니다."
                                            }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description  = "해당 User가 존재하지 않는 경우: us404",
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
    @PatchMapping("/users/{userId}/roles")
    ResponseEntity<UserResponse> assignAdminRole(@PathVariable("userId") Long userId);
}
