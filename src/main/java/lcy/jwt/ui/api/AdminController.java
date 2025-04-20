package lcy.jwt.ui.api;

import lcy.jwt.application.AdminService;
import lcy.jwt.domain.UserRole;
import lcy.jwt.dto.UserResponse;
import lcy.jwt.ui.docs.AdminControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Secured(UserRole.Authority.ADMIN)
public class AdminController implements AdminControllerDocs {
    private final AdminService adminService;

    @Override
    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<UserResponse> assignAdminRole(
            @PathVariable("userId") Long userId
    ) {
        return ResponseEntity.ok(adminService.assignAdminRole(userId));
    }
}
