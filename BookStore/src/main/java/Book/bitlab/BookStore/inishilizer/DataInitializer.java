package Book.bitlab.BookStore.inishilizer;


import Book.bitlab.BookStore.Entity.Role;
import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.repositories.RoleRepository;
import Book.bitlab.BookStore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {

        Role userRole = ensureRole("ROLE_USER");
        Role adminRole = ensureRole("ROLE_ADMIN");

        if (adminEmail == null || adminEmail.isBlank()) {
            log.info("app.admin.email is not set: admin user will not be created.");
            return;
        }

        String email = adminEmail.trim().toLowerCase();

        // Пайдаланушы бұрын тіркелген болса, тек ADMIN рөлін береміз
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            User user = existing.get();
            boolean alreadyAdmin = user.getRoles().stream()
                    .anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

            if (!alreadyAdmin) {
                user.getRoles().add(adminRole);
                userRepository.save(user);
                log.info("ROLE_ADMIN granted to {}", email);
            }
            return;
        }

        // Жаңа админ жасау
        if (adminPassword == null || adminPassword.length() < 8) {
            log.warn("app.admin.password must be at least 8 characters: admin user was not created.");
            return;
        }

        String username = adminUsername == null || adminUsername.isBlank()
                ? "admin"
                : adminUsername.trim();

        if (userRepository.existsByUsername(username)) {
            log.warn("Username '{}' is already taken: admin user was not created.", username);
            return;
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);

        User admin = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(adminPassword))
                .roles(roles)
                .build();

        userRepository.save(admin);
        log.info("Admin user created: {}", email);
    }

    private Role ensureRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));
    }
}