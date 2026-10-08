package Book.bitlab.BookStore.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegestrationDTO {

    private String userName;
    private String email;
    private String password;

}
