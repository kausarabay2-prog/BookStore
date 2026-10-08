package Book.bitlab.BookStore.controllers;

import Book.bitlab.BookStore.Entity.Cart;
import Book.bitlab.BookStore.dto.CartItemDto;
import Book.bitlab.BookStore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<Cart> createCart() {
        return ResponseEntity.ok(cartService.createCart());
    }

    @PostMapping("/{cartId}/add")
    public ResponseEntity<?> addToCart(
            @PathVariable Long cartId,
            @RequestParam Long bookId,
            @RequestParam Integer quanity
    ) {
        return ResponseEntity.ok(
                cartService.addToCart(cartId, bookId, quanity)
        );
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<List<CartItemDto>> getCart(
            @PathVariable Long cartId
    ) {
        return ResponseEntity.ok(cartService.getCart(cartId));
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> clearCart(
            @PathVariable Long cartId
    ) {
        cartService.clearCart(cartId);
        return ResponseEntity.ok("Cart cleared");
    }

}

