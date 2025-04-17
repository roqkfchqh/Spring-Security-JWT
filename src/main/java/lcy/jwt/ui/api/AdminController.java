package lcy.jwt.ui.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lcy.jwt.application.AdminService;
import lcy.jwt.domain.UserRole;
import lcy.jwt.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Secured(UserRole.Authority.ADMIN)
@Tag(name = "Admin", description = "관리자 관련 API")
public class AdminController {
    private final AdminService adminService;

    @Operation(
            summary = "ADMIN 권한 부여",
            description = "[userId] 번 유저에게 관리자 권한을 부여합니다."
    )
    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<UserResponse> assignAdminRole(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(adminService.assignAdminRole(userId));
    }
}
