package Book.bitlab.BookStore.controllers;

import Book.bitlab.BookStore.dto.BookDTO;
import Book.bitlab.BookStore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    // SELLER / ADMIN: өзі қосқан кітаптар
    @GetMapping("/mine")
    public ResponseEntity<List<BookDTO>> getMine(Authentication authentication) {
        return ResponseEntity.ok(bookService.getMine(authentication.getName()));
    }

    // Бір дүкеннің кітаптары (барлығына көрінеді)
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<BookDTO>> getByShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(bookService.getByShop(shopId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @PostMapping
    public ResponseEntity<BookDTO> create(
            @RequestBody BookDTO dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookService.create(dto, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> update(
            @PathVariable Long id,
            @RequestBody BookDTO dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookService.update(id, dto, authentication.getName(), isAdmin(authentication)));
    }

    // Біткен кітапқа дана қосу: amount санына көбейеді
    @PostMapping("/{id}/restock")
    public ResponseEntity<BookDTO> restock(
            @PathVariable Long id,
            @RequestParam Integer amount,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                bookService.restock(id, amount, authentication.getName(), isAdmin(authentication)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(
            @PathVariable Long id,
            Authentication authentication
    ) {
        bookService.delete(id, authentication.getName(), isAdmin(authentication));
        return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(bookService.search(keyword));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<BookDTO>> filterByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(bookService.filterByCategory(categoryId));
    }

    @GetMapping("/price")
    public ResponseEntity<List<BookDTO>> filterByPrice(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice
    ) {
        return ResponseEntity.ok(bookService.filterByPrice(minPrice, maxPrice));
    }
}