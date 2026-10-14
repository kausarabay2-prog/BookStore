package Book.bitlab.BookStore.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private String customerName;
    private String address;
    private Long cartId;

    private Double totalPrice;
    private String status;

    private String userEmail;
    private LocalDateTime createdAt;
}