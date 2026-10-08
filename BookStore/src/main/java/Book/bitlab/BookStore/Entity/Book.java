package Book.bitlab.BookStore.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    private Double price;

    private String description;

    private String imageURL;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}