package Book.bitlab.BookStore.Mapper;

import Book.bitlab.BookStore.Item.CartItem;
import Book.bitlab.BookStore.dto.CartItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "book.price", target = "price")
    @Mapping(source = "quantity", target = "quanity")
    CartItemDto toDto(CartItem cartItem);
}