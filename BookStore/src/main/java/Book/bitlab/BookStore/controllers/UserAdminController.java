package Book.bitlab.BookStore.controllers;

import Book.bitlab.BookStore.dto.AdminUserDto;
import Book.bitlab.BookStore.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Тек ADMIN (SecurityConfig-те /api/admin/** қорғалған)
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    @GetMapping
    public ResponseEntity<List<AdminUserDto>> getAll() {
        return ResponseEntity.ok(userAdminService.getAll());
    }

    // role: SELLER немесе ADMIN
    @PostMapping("/{id}/roles/{role}")
    public ResponseEntity<AdminUserDto> grant(
            @PathVariable Long id,
            @PathVariable String role
    ) {
        return ResponseEntity.ok(userAdminService.grant(id, role));
    }

    @DeleteMapping("/{id}/roles/{role}")
    public ResponseEntity<AdminUserDto> revoke(
            @PathVariable Long id,
            @PathVariable String role,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                userAdminService.revoke(id, role, authentication.getName()));
    }
}