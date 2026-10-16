package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.dto.AuthResponse;
import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.Security.JwtService;
import Book.bitlab.BookStore.dto.UserInfoResponse;
import Book.bitlab.BookStore.dto.RegestrationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;

    public User register(RegestrationDTO dto) {
        return userService.register(dto);
    }

    public AuthResponse login(String email, String password) {

        User user = userService.login(email, password);
        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName())//,берет их называние
                .toList();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(roles.toArray(String[]::new))//передаются роли пользователя.
                        .build();

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                token,
                new UserInfoResponse(user.getUsername(), user.getEmail(), roles)
        );
    }
}