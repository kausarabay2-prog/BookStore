package Book.bitlab.BookStore.dto;

// Дүкен ашу / өзгерту сұранысы
public record ShopRequest(
        String name,
        String description
) {}