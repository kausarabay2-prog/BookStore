package Book.bitlab.BookStore.repositories;

import Book.bitlab.BookStore.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Пайдаланушының өз тапсырыстары (жаңасы бірінші)
    List<Order> findByUser_EmailOrderByIdDesc(String email);
}