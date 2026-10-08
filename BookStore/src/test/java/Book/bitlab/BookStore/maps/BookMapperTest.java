package Book.bitlab.BookStore.maps;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Category;
import Book.bitlab.BookStore.Mapper.BookMapper;
import Book.bitlab.BookStore.dto.BookDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BookMapperTest {
  @Autowired
    private BookMapper mapper;
  @Test
    void toDto_mapsBookAndCategoryFields(){
      Category category = Category.builder()
              .id(7L)
              .name("Fantazy")
              .build();

       Book book = Book.builder().
                id(1L)
               .title("The Hobbit")
               .author("Tolkin")
               .price(6000.0)
               .description("A fantazy adventure")
               .imageURL("https://example.com/hobbit.jpg")
               .category(category)
               .build();
      BookDTO result = mapper.toDto(book);

      Assertions.assertEquals(1L, result.getId());
      Assertions.assertEquals("The Hobbit", result.getTitle());
      Assertions.assertEquals("J. R. R. Tolkien", result.getAuthor());
      Assertions.assertEquals(6000.0, result.getPrice());
      Assertions.assertEquals("A fantasy adventure", result.getDescription());
      Assertions.assertEquals("https://example.com/hobbit.jpg", result.getImageUrl());
      Assertions.assertEquals(7L, result.getCategoryId());
      Assertions.assertEquals("Fantasy", result.getCategoryName());
  }
  }

