package Book.bitlab.BookStore.dto;

// Себеттің бір жолы: фронтқа қажетті өрістер ғана
public record CartLineDto(
        Long bookId,
        String bookTitle,
        Double price,
        Integer quantity,
        Integer stock
) {}