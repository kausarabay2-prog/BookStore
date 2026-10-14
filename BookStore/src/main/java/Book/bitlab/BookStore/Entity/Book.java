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

    // Қоймадағы дана саны (ескі кітаптарда null болуы мүмкін, ол 0 деп есептеледі)
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Кітапты қосқан адам (сатушы немесе админ). Ескі кітаптарда null.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    // Кітап қай дүкенде сатылады (дүкені жоқ иелердің кітаптарында null)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;
}