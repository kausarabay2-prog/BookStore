package Book.bitlab.BookStore.Mapper;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Category;
import Book.bitlab.BookStore.Entity.Shop;
import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.dto.BookDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-13T19:13:39+0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Microsoft)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public BookDTO toDto(Book book) {
        if ( book == null ) {
            return null;
        }

        BookDTO.BookDTOBuilder bookDTO = BookDTO.builder();

        bookDTO.categoryId( bookCategoryId( book ) );
        bookDTO.categoryName( bookCategoryName( book ) );
        bookDTO.imageUrl( book.getImageURL() );
        bookDTO.ownerName( bookOwnerUsername( book ) );
        bookDTO.ownerEmail( bookOwnerEmail( book ) );
        bookDTO.shopId( bookShopId( book ) );
        bookDTO.shopName( bookShopName( book ) );
        bookDTO.id( book.getId() );
        bookDTO.title( book.getTitle() );
        bookDTO.author( book.getAuthor() );
        bookDTO.price( book.getPrice() );
        bookDTO.description( book.getDescription() );
        bookDTO.stock( book.getStock() );

        return bookDTO.build();
    }

    private Long bookCategoryId(Book book) {
        if ( book == null ) {
            return null;
        }
        Category category = book.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String bookCategoryName(Book book) {
        if ( book == null ) {
            return null;
        }
        Category category = book.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String bookOwnerUsername(Book book) {
        if ( book == null ) {
            return null;
        }
        User owner = book.getOwner();
        if ( owner == null ) {
            return null;
        }
        String username = owner.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }

    private String bookOwnerEmail(Book book) {
        if ( book == null ) {
            return null;
        }
        User owner = book.getOwner();
        if ( owner == null ) {
            return null;
        }
        String email = owner.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private Long bookShopId(Book book) {
        if ( book == null ) {
            return null;
        }
        Shop shop = book.getShop();
        if ( shop == null ) {
            return null;
        }
        Long id = shop.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String bookShopName(Book book) {
        if ( book == null ) {
            return null;
        }
        Shop shop = book.getShop();
        if ( shop == null ) {
            return null;
        }
        String name = shop.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
