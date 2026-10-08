package Book.bitlab.BookStore.repositories;

import Book.bitlab.BookStore.Entity.Promission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PremissionREpisytory extends JpaRepository<Promission,Long> {

    Optional<Promission> findByName(String name);
}
