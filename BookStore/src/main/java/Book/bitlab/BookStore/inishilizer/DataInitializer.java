package Book.bitlab.BookStore.inishilizer;

import Book.bitlab.BookStore.Entity.Role;
import Book.bitlab.BookStore.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        createRoleIfMissing("ROLE_USER");
        createRoleIfMissing("ROLE_ADMIN");
    }

    private void createRoleIfMissing(String name) {
        if (roleRepository.findByName(name).isEmpty()) {
            roleRepository.save(Role.builder().name(name).build());
        }
    }
}