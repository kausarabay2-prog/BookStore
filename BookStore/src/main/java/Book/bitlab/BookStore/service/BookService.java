package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Category;
import Book.bitlab.BookStore.Mapper.BookMapper;
import Book.bitlab.BookStore.dto.BookDTO;
import Book.bitlab.BookStore.repositories.BookRepository;
import Book.bitlab.BookStore.repositories.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;

    public BookService(
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            BookMapper bookMapper
    ) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookMapper = bookMapper;
    }

    private ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    private Book find(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<BookDTO> getAll() {
        return bookRepository.findAll().stream().map(bookMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public BookDTO getById(Long id) {
        return bookMapper.toDto(find(id));
    }

    private boolean hasCategory(BookDTO dto) {
        return dto.getCategoryId() != null
                || (dto.getCategoryName() != null && !dto.getCategoryName().isBlank());
    }

    private Category getCategory(BookDTO dto) {
        if (dto.getCategoryId() != null) {
            return categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Category not found: " + dto.getCategoryId()));
        }

        if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {
            String name = dto.getCategoryName().trim();
            return categoryRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> categoryRepository.save(Category.builder().name(name).build()));
        }

        throw bad("Category is required. Send categoryId or categoryName");
    }

    private void validate(BookDTO dto) {
        if (dto == null) throw bad("Book data is required");
        if (dto.getTitle() == null || dto.getTitle().isBlank()) throw bad("Title must not be empty");
        if (dto.getAuthor() == null || dto.getAuthor().isBlank()) throw bad("Author must not be empty");
        if (dto.getPrice() == null || !Double.isFinite(dto.getPrice()) || dto.getPrice() < 0) {
            throw bad("Price must be zero or greater");
        }
    }

    public BookDTO create(BookDTO dto) {
        validate(dto);

        Book book = Book.builder()
                .title(dto.getTitle().trim())
                .author(dto.getAuthor().trim())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .imageURL(dto.getImageUrl())
                .category(getCategory(dto))
                .build();

        return bookMapper.toDto(bookRepository.save(book));
    }

    public BookDTO update(Long id, BookDTO dto) {
        Book book = find(id);
        validate(dto);

        // Категория келмесе, ескісі сақталады
        if (hasCategory(dto)) {
            book.setCategory(getCategory(dto));
        }

        book.setTitle(dto.getTitle().trim());
        book.setAuthor(dto.getAuthor().trim());
        book.setPrice(dto.getPrice());
        book.setDescription(dto.getDescription());

        // Сурет жіберілмесе, ескі сурет өшіп кетпейді
        if (dto.getImageUrl() != null) {
            book.setImageURL(dto.getImageUrl());
        }

        return bookMapper.toDto(bookRepository.save(book));
    }

    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<BookDTO> search(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword == null ? "" : keyword)
                .stream().map(bookMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<BookDTO> filterByCategory(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId)
                .stream().map(bookMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<BookDTO> filterByPrice(Double minPrice, Double maxPrice) {
        return bookRepository.findByPriceBetween(minPrice, maxPrice)
                .stream().map(bookMapper::toDto).toList();
    }
}