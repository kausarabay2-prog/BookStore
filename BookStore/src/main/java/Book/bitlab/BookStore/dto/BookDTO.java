package Book.bitlab.BookStore.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private Double price;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;

    // Қоймадағы саны
    private Integer stock;

    // Кітапты қосқан адам (тек оқуға: сұраныстан алынбайды)
    private String ownerName;
    private String ownerEmail;

    // Дүкен (тек оқуға)
    private Long shopId;
    private String shopName;
}