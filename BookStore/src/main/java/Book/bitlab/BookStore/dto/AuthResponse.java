package Book.bitlab.BookStore.dto;

public record AuthResponse(
        String token,
        UserInfoResponse user
) {}