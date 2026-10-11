package Book.bitlab.BookStore.Mapper;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Category;
import Book.bitlab.BookStore.dto.BookDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-10T17:00:23+0500",
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
        bookDTO.id( book.getId() );
        bookDTO.title( book.getTitle() );
        bookDTO.author( book.getAuthor() );
        bookDTO.price( book.getPrice() );
        bookDTO.description( book.getDescription() );

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
}
