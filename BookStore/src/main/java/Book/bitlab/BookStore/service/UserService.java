package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Role;
import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.dto.RegestrationDTO;
import Book.bitlab.BookStore.repositories.RoleRepository;
import Book.bitlab.BookStore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public User register(RegestrationDTO dto) {

        // DTO және өрістерді тексеру
        if (dto == null
                || dto.getEmail() == null
                || dto.getEmail().isBlank()
                || dto.getUserName() == null
                || dto.getUserName().isBlank()
                || dto.getPassword() == null
                || dto.getPassword().length() < 8) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email, username және кемінде 8 таңбалы пароль енгізіңіз"
            );
        }

        String email = dto.getEmail().trim().toLowerCase();
        String username = dto.getUserName().trim();

        // Email бұрын тіркелген бе?
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Бұл email бұрын тіркелген"
            );
        }

        // Username бұрын тіркелген бе?
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Бұл username бұрын тіркелген"
            );
        }

        // Жаңа пайдаланушыға ROLE_USER беру
        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "ROLE_USER рөлі табылмады"
                ));

        // Парольді хэштеу және пайдаланушыны жасау
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles(new HashSet<>(Set.of(role)))
                .build();

        return userRepository.save(user);
    }


    public User login(String email, String password) {

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Email немесе пароль қате"
            );
        }

        // Email бойынша пайдаланушыны табу
        User user = userRepository.findByEmail(
                email.trim().toLowerCase()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Email немесе пароль қате"
        ));

        // Енгізілген парольді сақталған хэшпен салыстыру
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Email немесе пароль қате"
            );
        }

        return user;
    }
}
