package Book.bitlab.BookStore.dto;

import java.util.List;

public record UserInfoResponse(
        String username,
        String email,
        List<String> roles
) {}