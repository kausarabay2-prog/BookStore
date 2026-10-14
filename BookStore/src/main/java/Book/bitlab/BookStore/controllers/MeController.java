package Book.bitlab.BookStore.controllers;

import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.dto.UserInfoResponse;
import Book.bitlab.BookStore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

// Ағымдағы пайдаланушының ЖАҢА деректері (рөлдер өзгерсе, қайта кірмей-ақ жаңарады)
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final UserRepository userRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<UserInfoResponse> me(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));

        return ResponseEntity.ok(new UserInfoResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(r -> r.getName()).toList()
        ));
    }
}