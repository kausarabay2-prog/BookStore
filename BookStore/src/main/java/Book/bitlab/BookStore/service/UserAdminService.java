package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Role;
import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.dto.AdminUserDto;
import Book.bitlab.BookStore.repositories.RoleRepository;
import Book.bitlab.BookStore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    // Тек осы рөлдерді беруге/алуға болады. ROLE_USER әрқашан қалады.
    private static final Set<String> MANAGEABLE = Set.of("ROLE_ADMIN", "ROLE_SELLER");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private AdminUserDto toDto(User user) {
        return new AdminUserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getName).sorted().toList()
        );
    }

    private String normalize(String role) {
        if (role == null || role.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
        }
        String name = role.trim().toUpperCase();
        if (!name.startsWith("ROLE_")) {
            name = "ROLE_" + name;
        }
        if (!MANAGEABLE.contains(name)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only ADMIN and SELLER roles can be managed");
        }
        return name;
    }

    private User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<AdminUserDto> getAll() {
        return userRepository.findAll(Sort.by("id")).stream().map(this::toDto).toList();
    }

    @Transactional
    public AdminUserDto grant(Long userId, String role) {
        String name = normalize(role);
        User user = find(userId);

        boolean has = user.getRoles().stream().anyMatch(r -> name.equals(r.getName()));
        if (!has) {
            Role entity = roleRepository.findByName(name)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));
            user.getRoles().add(entity);
            userRepository.save(user);
        }
        return toDto(user);
    }

    @Transactional
    public AdminUserDto revoke(Long userId, String role, String currentEmail) {
        String name = normalize(role);
        User user = find(userId);

        // Админ өз ADMIN рөлін алып тастап, жүйеден құлыптанып қалмасын
        if ("ROLE_ADMIN".equals(name) && user.getEmail() != null
                && user.getEmail().equalsIgnoreCase(currentEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "You cannot remove your own admin role");
        }

        user.getRoles().removeIf(r -> name.equals(r.getName()));
        userRepository.save(user);
        return toDto(user);
    }
}