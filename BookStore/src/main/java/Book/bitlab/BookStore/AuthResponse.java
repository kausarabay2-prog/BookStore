package Book.bitlab.BookStore;

public record AuthResponse(
        String token,
        UserInfoResponse user
) {}