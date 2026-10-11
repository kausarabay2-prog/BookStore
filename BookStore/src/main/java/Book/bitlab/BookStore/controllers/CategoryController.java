package Book.bitlab.BookStore.controllers;
import Book.bitlab.BookStore.dto.CategoryDTO; import Book.bitlab.BookStore.service.CategoryService; import lombok.RequiredArgsConstructor; import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAll() {
        return ResponseEntity.ok(
                categoryService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                categoryService.getById(id)
        );
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> create(
            @RequestBody CategoryDTO dto
    ) {
        return ResponseEntity.ok(
                categoryService.create(dto)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(
            @PathVariable Long id,
            @RequestBody CategoryDTO dto
    ) {
        return ResponseEntity.ok(
                categoryService.update(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {
        categoryService.delete(id);

        return ResponseEntity.ok(
                "Category deleted successfully"
        );
    }
}
