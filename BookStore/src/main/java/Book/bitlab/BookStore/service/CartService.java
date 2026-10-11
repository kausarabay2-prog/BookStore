package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Cart;
import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.Item.CartItem;
import Book.bitlab.BookStore.Mapper.CartMapper;
import Book.bitlab.BookStore.dto.CartItemDto;
import Book.bitlab.BookStore.repositories.BookRepository;
import Book.bitlab.BookStore.repositories.CartRepository;
import Book.bitlab.BookStore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    private Cart getOwnedCart(Long cartId, String email) {
        return cartRepository.findByIdAndUser_Email(cartId, email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "You do not have access to this cart"));
    }

    @Transactional
    public Cart createCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));

        return cartRepository.save(Cart.builder().user(user).build());
    }

    @Transactional
    public CartItemDto addToCart(Long cartId, Long bookId, Integer quantity, String email) {

        if (quantity == null || quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
        }

        Cart cart = getOwnedCart(cartId, email);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found"));

        // Сол кітап бұрын қосылған болса, санын көбейтеміз (қайталанбайды)
        CartItem target = null;
        for (CartItem existing : cart.getItems()) {
            if (existing.getBook() != null && existing.getBook().getId().equals(bookId)) {
                existing.setQuantity(existing.getQuantity() + quantity);
                target = existing;
                break;
            }
        }

        if (target == null) {
            target = CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .quantity(quantity)
                    .build();
            cart.getItems().add(target);
        }

        Cart saved = cartRepository.saveAndFlush(cart);

        CartItem result = saved.getItems().stream()
                .filter(i -> i.getBook() != null && i.getBook().getId().equals(bookId))
                .findFirst()
                .orElse(target);

        return cartMapper.toDto(result);
    }

    @Transactional(readOnly = true)
    public List<CartItemDto> getCart(Long cartId, String email) {
        return getOwnedCart(cartId, email).getItems()
                .stream()
                .map(cartMapper::toDto)
                .toList();
    }

    @Transactional
    public void clearCart(Long cartId, String email) {
        Cart cart = getOwnedCart(cartId, email);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}