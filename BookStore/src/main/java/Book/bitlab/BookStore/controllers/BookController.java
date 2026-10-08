package Book.bitlab.BookStore.controllers;

import Book.bitlab.BookStore.dto.BookDTO; import Book.bitlab.BookStore.service.BookService; import lombok.RequiredArgsConstructor; import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {


    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    @GetMapping("/{id}")public ResponseEntity<BookDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                bookService.getById(id)
         );
    }

    @PostMapping
    public ResponseEntity<BookDTO> create(
            @RequestBody BookDTO dto
    ) {
        return ResponseEntity.ok(
                bookService.create(dto)
        );
    }

    @PutMapping("/{id}")public ResponseEntity<BookDTO> update(
            @PathVariable Long id,
            @RequestBody BookDTO dto
    ) {
        return ResponseEntity.ok(
                bookService.update(id, dto)
        );
    }

    @DeleteMapping("/{id}")public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {
        bookService.delete(id);

        return ResponseEntity.ok(
                "Book deleted successfully"
        );
    }

    @GetMapping("/search")public ResponseEntity<List<BookDTO>> search(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(
                bookService.search(keyword)
        );
    }

    @GetMapping("/category/{categoryId}")public ResponseEntity<List<BookDTO>> filterByCategory(
            @PathVariable Long categoryId
    ) {
        return ResponseEntity.ok(
                bookService.filterByCategory(categoryId)
        );
    }

    @GetMapping("/price")public ResponseEntity<List<BookDTO>> filterByPrice(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice
    ) {
        return ResponseEntity.ok(
                bookService.filterByPrice(
                        minPrice,
                        maxPrice
                )
        );
    }
}