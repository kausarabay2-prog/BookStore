package Book.bitlab.BookStore.Mapper;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.dto.BookDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "imageURL", target = "imageUrl")
    BookDTO toDto(Book book);
}