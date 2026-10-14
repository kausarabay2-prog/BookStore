package Book.bitlab.BookStore.controllers;

import Book.bitlab.BookStore.dto.AuthResponse;
import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.dto.LoginDto;
import Book.bitlab.BookStore.dto.RegestrationDTO;
import Book.bitlab.BookStore.dto.UserResponseDTO;
import Book.bitlab.BookStore.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @RequestBody RegestrationDTO dto) {

        User user = authService.register(dto);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRoles(
                user.getRoles()
                        .stream()
                        .map(role -> role.getName())
                        .toList()
        );

        return ResponseEntity.ok(response);
    }

    // Жауап: { "token": "...", "user": { "username", "email", "roles" } }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginDto dto) {

        return ResponseEntity.ok(

                authService.login(dto.getEmail(), dto.getPassword())
        );
    }
}