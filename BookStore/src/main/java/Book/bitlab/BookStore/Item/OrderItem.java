package Book.bitlab.BookStore.Item;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Order;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Integer quanity;
    private Double price;

    @ManyToOne
    private Book book;
    @ManyToOne
    private Order order;

}
