package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Category;
import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.Mapper.BookMapper;
import Book.bitlab.BookStore.dto.BookDTO;
import Book.bitlab.BookStore.repositories.BookRepository;
import Book.bitlab.BookStore.repositories.CategoryRepository;
import Book.bitlab.BookStore.repositories.ShopRepository;
import Book.bitlab.BookStore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
//Ограничения количества книг
    private static final int MAX_STOCK = 1_000_000;
    private static final int MAX_RESTOCK = 100_000;

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final BookMapper bookMapper;
    private ResponseStatusException bad(String msg) {

        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    private Book find(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found: " + id));
    }

    private void assertCanManage(Book book, String email, boolean admin) {
        if (admin) return;

        User owner = book.getOwner();
        if (owner == null || owner.getEmail() == null || !owner.getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You can manage only your own books");
        }
    }

    private int validStock(Integer value) {
        if (value == null) return 0;
        if (value < 0 || value > MAX_STOCK) {
            throw bad("Stock must be between 0 and " + MAX_STOCK);
        }
        return value;
    }
//Получение списка книг
    @Transactional(readOnly = true)
    public List<BookDTO> getAll() {
        return bookRepository.
                findAll()
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookDTO> getMine(String email) {
        return bookRepository.findByOwner_EmailOrderByIdDesc(email)
                .stream().map(bookMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<BookDTO> getByShop(Long shopId) {
        return bookRepository.findByShop_IdOrderByIdDesc(shopId)
                .stream().map(bookMapper::toDto).toList();
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
        if (dto.getTitle() == null ||
                dto.getTitle().isBlank())
            throw bad("Title must not be empty");
        if (dto.getAuthor() == null || dto.getAuthor().isBlank()) throw bad("Author must not be empty");
        if (dto.getPrice() == null || !Double.isFinite(dto.getPrice()) || dto.getPrice() < 0) {
            throw bad("Price must be zero or greater");
        }
    }
    public BookDTO create(BookDTO dto, String email) {
        validate(dto);
//находит пользователя, который будет её владельцем.
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
        //Далее создаётся объект:
        Book book = Book.builder()
                .title(dto.getTitle().trim())
                .author(dto.getAuthor().trim())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .imageURL(dto.getImageUrl())
                .stock(validStock(dto.getStock()))
                .category(getCategory(dto))
                .owner(owner)
                // Иесінің дүкені болса, кітап сол дүкенде көрінеді
                .shop(shopRepository.findByOwner_Email(email).orElse(null))
                .build();

        return bookMapper.toDto(bookRepository.save(book));
    }

    public BookDTO update(Long id, BookDTO dto, String email, boolean admin) {
        Book book = find(id);
        assertCanManage(book, email, admin);
        validate(dto);

        if (hasCategory(dto)) {
            book.setCategory(getCategory(dto));
        }
        book.setTitle(dto.getTitle().trim());
        book.setAuthor(dto.getAuthor().trim());
        book.setPrice(dto.getPrice());
        book.setDescription(dto.getDescription());

        if (dto.getImageUrl() != null) {
            book.setImageURL(dto.getImageUrl());
        }

        if (dto.getStock() != null) {
            book.setStock(validStock(dto.getStock()));
        }

        return bookMapper.toDto(bookRepository.save(book));
    }

    // Пополнение склада
    public BookDTO restock(Long id, Integer amount, String email, boolean admin) {
        if (amount == null || amount < 1 || amount > MAX_RESTOCK) {
            throw bad("Amount must be between 1 and " + MAX_RESTOCK);
        }

        Book book = find(id);
        assertCanManage(book, email, admin);

        int current = book.getStock() == null ? 0 : book.getStock();
        book.setStock(current + amount);

        return bookMapper.toDto(bookRepository.save(book));
    }

    public void delete(Long id, String email, boolean admin) {
        Book book = find(id);
        assertCanManage(book, email, admin);
        bookRepository.delete(book);
    }

    @Transactional(readOnly = true)
    public List<BookDTO> search(String keyword) {
        String k = keyword == null ? "" : keyword.trim();

        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(k, k)
                .stream()
                .map(bookMapper::toDto)
                .toList();
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