package Book.bitlab.BookStore.controllers;

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
    public ResponseEntity<?> register(@RequestBody RegestrationDTO dto) {
        try {
            User user = authService.register(dto);
            return ResponseEntity.ok(toResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        try {
            User user = authService.login(dto.getEmail(), dto.getPassword());
            return ResponseEntity.ok(toResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private UserResponseDTO toResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .toList())
                .build();
    }
    }