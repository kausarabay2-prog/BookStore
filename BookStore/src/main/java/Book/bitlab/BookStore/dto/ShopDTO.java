package Book.bitlab.BookStore.dto;

public record ShopDTO(
        Long id,
        String name,
        String description,
        String ownerName
) {}