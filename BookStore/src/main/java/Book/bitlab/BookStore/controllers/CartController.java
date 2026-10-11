package Book.bitlab.BookStore.controllers;

import Book.bitlab.BookStore.dto.CartItemDto;
import Book.bitlab.BookStore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Cart entity ішінде User және password hash сыртқа шықпайды, тек id
    @PostMapping
    public ResponseEntity<Map<String, Long>> createCart(Authentication authentication) {
        Long id = cartService.createCart(authentication.getName()).getId();
        return ResponseEntity.ok(Map.of("id", id));
    }

    @PostMapping("/{cartId}/add")
    public ResponseEntity<CartItemDto> addToCart(
            @PathVariable Long cartId,
            @RequestParam Long bookId,
            @RequestParam Integer quantity,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cartService.addToCart(cartId, bookId, quantity, authentication.getName()));
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<List<CartItemDto>> getCart(
            @PathVariable Long cartId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(cartService.getCart(cartId, authentication.getName()));
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Map<String, String>> clearCart(
            @PathVariable Long cartId,
            Authentication authentication
    ) {
        cartService.clearCart(cartId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Cart cleared"));
    }
}