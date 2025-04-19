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
@ApiResponse(responseCode = "403", description = "권한 없음",
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
            @ApiResponse(responseCode = "400", description = "잘못된 요청(이미 ADMIN, 해당 user 존재하지 않음)",
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
