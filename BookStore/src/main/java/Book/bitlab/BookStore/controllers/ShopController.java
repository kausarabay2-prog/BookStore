package Book.bitlab.BookStore.controllers;

import Book.bitlab.BookStore.dto.ShopDTO;
import Book.bitlab.BookStore.dto.ShopRequest;
import Book.bitlab.BookStore.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    // Барлығына көрінеді
    @GetMapping
    public ResponseEntity<List<ShopDTO>> getAll() {
        return ResponseEntity.ok(shopService.getAll());
    }

    // Менің дүкенім (кірген пайдаланушы). Жоқ болса 404.
    @GetMapping("/mine")
    public ResponseEntity<ShopDTO> mine(Authentication authentication) {
        return ResponseEntity.ok(shopService.getMine(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.getById(id));
    }

    // Өз дүкенімді ашу: кез келген кірген пайдаланушы
    @PostMapping
    public ResponseEntity<ShopDTO> open(
            @RequestBody ShopRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(shopService.open(authentication.getName(), request));
    }

    @PutMapping("/mine")
    public ResponseEntity<ShopDTO> updateMine(
            @RequestBody ShopRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(shopService.updateMine(authentication.getName(), request));
    }
}