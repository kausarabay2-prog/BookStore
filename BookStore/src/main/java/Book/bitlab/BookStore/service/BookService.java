package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Category;
import Book.bitlab.BookStore.Mapper.BookMapper;
import Book.bitlab.BookStore.dto.BookDTO;
import Book.bitlab.BookStore.repositories.BookRepository;
import Book.bitlab.BookStore.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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

    public List<BookDTO> getAll() {

        List<Book> books = bookRepository.findAll();

        List<BookDTO> result = new ArrayList<>();

        for (Book book : books) {
            result.add(bookMapper.toDto(book));
        }

        return result;
    }

    public BookDTO getById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Book not found"));

        return bookMapper.toDto(book);
    }

    // =========================
    // CREATE BOOK
    // =========================

    public BookDTO create(BookDTO dto) {

        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new RuntimeException("Title must not be empty");
        }

        if (dto.getAuthor() == null || dto.getAuthor().isBlank()) {
            throw new RuntimeException("Author must not be empty");
        }

        if (dto.getPrice() == null || dto.getPrice() < 0) {
            throw new RuntimeException("Price must be correct");
        }

        if (dto.getCategoryName() == null ||
                dto.getCategoryName().isBlank()) {

            throw new RuntimeException("Category name must not be empty");
        }

        // Category іздеу
        Category category = categoryRepository
                .findByNameIgnoreCase(dto.getCategoryName())
                .orElseGet(() -> {

                    // Егер жоқ болса — жаңасын жасау
                    Category newCategory = Category.builder()
                            .name(dto.getCategoryName())
                            .build();

                    return categoryRepository.save(newCategory);
                });

        // Book жасау
        Book book = Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .imageURL(dto.getImageUrl())
                .category(category)
                .build();

        Book savedBook = bookRepository.save(book);

        return bookMapper.toDto(savedBook);
    }

    // =========================
    // UPDATE BOOK
    // =========================

    public BookDTO update(Long id, BookDTO dto) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Book not found"));

        Category category = categoryRepository
                .findById(dto.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPrice(dto.getPrice());
        book.setDescription(dto.getDescription());
        book.setImageURL(dto.getImageUrl());
        book.setCategory(category);

        return bookMapper.toDto(
                bookRepository.save(book)
        );
    }

    // =========================
    // DELETE
    // =========================

    public void delete(Long id) {

        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found");
        }

        bookRepository.deleteById(id);
    }

    // =========================
    // SEARCH
    // =========================

    public List<BookDTO> search(String keyword) {

        List<Book> books =
                bookRepository
                        .findByTitleContainingIgnoreCase(keyword);

        List<BookDTO> result = new ArrayList<>();

        for (Book book : books) {
            result.add(bookMapper.toDto(book));
        }

        return result;
    }

    // =========================
    // FILTER CATEGORY
    // =========================

    public List<BookDTO> filterByCategory(Long category_Id) {

        List<Book> books =
                bookRepository.findByCategoryId(category_Id);

        List<BookDTO> result = new ArrayList<>();

        for (Book book : books) {
            result.add(bookMapper.toDto(book));
        }

        return result;
    }

    // =========================
    // FILTER PRICE
    // =========================

    public List<BookDTO> filterByPrice(
            Double minPrice,
            Double maxPrice
    ) {

        List<Book> books =
                bookRepository.findByPriceBetween(
                        minPrice,
                        maxPrice
                );

        List<BookDTO> result = new ArrayList<>();

        for (Book book : books) {
            result.add(bookMapper.toDto(book));
        }

        return result;
    }
}