package lcy.jwt.ui.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lcy.jwt.application.AdminService;
import lcy.jwt.domain.UserRole;
import lcy.jwt.dto.UserResponse;
import lcy.jwt.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Secured(UserRole.Authority.ADMIN)
@Tag(name = "Admin", description = "관리자 관련 API")
@ApiResponse(responseCode = "401", description = "로그인 필요: se401",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
)
@ApiResponse(responseCode = "403", description = "ADMIN 권한 없음: se403",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
)
public class AdminController {
    private final AdminService adminService;

    @Operation(
            summary = "관리자 권한 부여",
            description = "지정한 유저 ID에 ADMIN 권한을 부여합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "권한 부여 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "이미 ADMIN 인 경우: ad400",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "해당 User 가 존재하지 않는 경우: us404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
    })
    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<UserResponse> assignAdminRole(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(adminService.assignAdminRole(userId));
    }
}
