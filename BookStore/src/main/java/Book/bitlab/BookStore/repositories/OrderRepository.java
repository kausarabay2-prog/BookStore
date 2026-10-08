package Book.bitlab.BookStore.repositories;

import Book.bitlab.BookStore.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
