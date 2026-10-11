package Book.bitlab.BookStore.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private String customerName;   // custumerName емес
    private String address;
    private Long cartId;

    private Double totalPrice;
    private String status;
}

