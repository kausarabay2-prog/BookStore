package Book.bitlab.BookStore.controllers;

import Book.bitlab.BookStore.dto.BookDTO;
import Book.bitlab.BookStore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final BookService bookService;
    @GetMapping("books")
    public ResponseEntity<List<BookDTO>> getBooks(){
        return ResponseEntity.ok(bookService.getAll());
    }
    @PostMapping("/books")
    public ResponseEntity<?> addBooks(@RequestBody BookDTO bookDTO
    ){
        return  ResponseEntity.ok(bookService.create(bookDTO));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id,
                                        @RequestBody BookDTO bookDTO){
        return ResponseEntity.ok(bookService.update(id, bookDTO));

    }
    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id){
        bookService.delete(id);
        return  ResponseEntity.ok("Book deleted stccesfully");
    }

}
