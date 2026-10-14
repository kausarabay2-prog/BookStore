package Book.bitlab.BookStore.controllers;

import Book.bitlab.BookStore.dto.OrderDTO;
import Book.bitlab.BookStore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Жаңа тапсырыс: тек өз себетінен
    @PostMapping
    public ResponseEntity<OrderDTO> create(
            @RequestBody OrderDTO dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(orderService.create(dto, authentication.getName()));
    }

    // Кірген пайдаланушының өз тапсырыстары
    @GetMapping("/my")
    public ResponseEntity<List<OrderDTO>> getMine(Authentication authentication) {
        return ResponseEntity.ok(orderService.getMine(authentication.getName()));
    }

    // ADMIN
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    // ADMIN
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    // ADMIN
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}