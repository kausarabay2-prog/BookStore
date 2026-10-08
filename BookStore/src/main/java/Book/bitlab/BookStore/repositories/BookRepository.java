package Book.bitlab.BookStore.repositories;

import Book.bitlab.BookStore.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByCategoryId(Long categoryId);
    List<Book> findByPriceBetween(Double minPrice, Double maxPrice);

    List<Book> category_Id(Long categoryId);
}
