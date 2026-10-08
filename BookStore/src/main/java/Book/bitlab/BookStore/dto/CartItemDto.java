package Book.bitlab.BookStore.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private  Integer quanity;
    private Double price;

}
