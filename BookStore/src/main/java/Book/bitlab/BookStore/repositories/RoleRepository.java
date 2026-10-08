package Book.bitlab.BookStore.repositories;

import Book.bitlab.BookStore.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findRoleByName(String name);

    Optional<Role> findByName(String name);

}
