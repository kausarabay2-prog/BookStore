package Book.bitlab.BookStore.service;
import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Category;
import Book.bitlab.BookStore.Mapper.BookMapper;
import Book.bitlab.BookStore.dto.BookDTO;
import Book.bitlab.BookStore.dto.CategoryDTO;
import Book.bitlab.BookStore.repositories.BookRepository;
import Book.bitlab.BookStore.repositories.CategoryRepository; import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public BookDTO create(BookDTO dto) {

        Category category =
                categoryRepository
                        .findByNameIgnoreCase(dto.getCategoryName())
                        .orElseGet(() ->
                                categoryRepository.save(
                                        Category.builder()
                                                .name(dto.getCategoryName())
                                                .build()
                                )
                        );

        Book book = Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .imageURL(dto.getImageUrl())
                .category(category)
                .build();

        return bookMapper.toDto(
                bookRepository.save(book)
        );
    }

    public Category create(CategoryDTO dto) {

        Category category = Category.builder()
                .name(dto.getName())
                .build();

        return categoryRepository.save(category);
    }
    public Category getById(Long id) {

        return categoryRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));
    }

    public Category update(Long id, CategoryDTO dto) {

        Category category = getById(id);

        category.setName(dto.getName());

        return categoryRepository.save(category);
    }

    public void delete(Long id) {

        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }

        categoryRepository.deleteById(id);
    }
}