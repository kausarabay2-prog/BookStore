package Book.bitlab.BookStore.repositories;

import Book.bitlab.BookStore.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {

}
