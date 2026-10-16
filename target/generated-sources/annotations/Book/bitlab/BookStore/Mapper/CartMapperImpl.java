package Book.bitlab.BookStore.Mapper;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Item.CartItem;
import Book.bitlab.BookStore.dto.CartItemDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-15T21:59:55+0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Microsoft)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartItemDto toDto(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemDto.CartItemDtoBuilder cartItemDto = CartItemDto.builder();

        cartItemDto.bookId( cartItemBookId( cartItem ) );
        cartItemDto.bookTitle( cartItemBookTitle( cartItem ) );
        cartItemDto.price( cartItemBookPrice( cartItem ) );
        cartItemDto.quanity( cartItem.getQuantity() );
        cartItemDto.id( cartItem.getId() );

        return cartItemDto.build();
    }

    private Long cartItemBookId(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        Book book = cartItem.getBook();
        if ( book == null ) {
            return null;
        }
        Long id = book.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String cartItemBookTitle(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        Book book = cartItem.getBook();
        if ( book == null ) {
            return null;
        }
        String title = book.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }

    private Double cartItemBookPrice(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        Book book = cartItem.getBook();
        if ( book == null ) {
            return null;
        }
        Double price = book.getPrice();
        if ( price == null ) {
            return null;
        }
        return price;
    }
}
