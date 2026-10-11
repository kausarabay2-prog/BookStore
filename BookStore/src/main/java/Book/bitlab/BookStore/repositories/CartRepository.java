package Book.bitlab.BookStore.repositories;

import Book.bitlab.BookStore.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByIdAndUser_Email(Long id, String email);
}