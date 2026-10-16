package Book.bitlab.BookStore.repositories;

import Book.bitlab.BookStore.Entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    // Атауы НЕМЕСЕ авторы бойынша іздеу
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

    List<Book> findByCategoryId(Long categoryId);


    List<Book> findByPriceBetween(Double minPrice, Double maxPrice);

    // Сатушының өз кітаптары
    List<Book> findByOwner_EmailOrderByIdDesc(String email);

    // Бір дүкеннің кітаптары
    List<Book> findByShop_IdOrderByIdDesc(Long shopId);

    // Тапсырыс кезінде қойманы бір мезгілде екі адам азайтып жібермеуі үшін қамау.
    // id бойынша сұрыпталады, сонда deadlock болмайды.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id in :ids order by b.id")
    List<Book> findAllByIdForUpdate(@Param("ids") Collection<Long> ids);
}