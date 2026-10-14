package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Cart;
import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.Item.CartItem;
import Book.bitlab.BookStore.Mapper.CartMapper;
import Book.bitlab.BookStore.dto.CartItemDto;
import Book.bitlab.BookStore.dto.CartLineDto;
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

    private static final int MAX_QUANTITY = 99;

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    private Cart getOwnedCart(Long cartId, String email) {
        return cartRepository.findByIdAndUser_Email(cartId, email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "You do not have access to this cart"));
    }

    private int stockOf(Book book) {
        return book == null || book.getStock() == null ? 0 : book.getStock();
    }

    private CartLineDto toLine(CartItem item) {
        Book book = item.getBook();
        return new CartLineDto(
                book == null ? null : book.getId(),
                book == null ? null : book.getTitle(),
                book == null ? null : book.getPrice(),
                item.getQuantity(),
                stockOf(book)
        );
    }

    private List<CartLineDto> toLines(Cart cart) {
        return cart.getItems().stream().map(this::toLine).toList();
    }

    private CartItem findItem(Cart cart, Long bookId) {
        return cart.getItems().stream()
                .filter(i -> i.getBook() != null && i.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book is not in the cart"));
    }

    private ResponseStatusException outOfStock(Book book) {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Oops! \"" + book.getTitle() + "\" is out of stock. Please try again later.");
    }

    private ResponseStatusException notEnough(Book book, int stock) {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Oops! Only " + stock + " cop" + (stock == 1 ? "y" : "ies")
                        + " of \"" + book.getTitle() + "\" left in stock.");
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

        if (quantity == null || quantity <= 0 || quantity > MAX_QUANTITY) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Quantity must be between 1 and " + MAX_QUANTITY);
        }

        Cart cart = getOwnedCart(cartId, email);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Book not found"));

        int stock = stockOf(book);
        if (stock <= 0) {
            throw outOfStock(book);
        }

        // Сол кітап бұрын қосылған болса, санын көбейтеміз
        CartItem target = null;
        for (CartItem existing : cart.getItems()) {
            if (existing.getBook() != null && existing.getBook().getId().equals(bookId)) {
                target = existing;
                break;
            }
        }

        int newQty = (target == null ? 0 : target.getQuantity()) + quantity;
        if (newQty > MAX_QUANTITY) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Maximum " + MAX_QUANTITY + " copies per book");
        }
        if (newQty > stock) {
            throw notEnough(book, stock);
        }

        if (target == null) {
            target = CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .quantity(quantity)
                    .build();
            cart.getItems().add(target);
        } else {
            target.setQuantity(newQty);
        }

        Cart saved = cartRepository.saveAndFlush(cart);

        CartItem result = saved.getItems().stream()
                .filter(i -> i.getBook() != null && i.getBook().getId().equals(bookId))
                .findFirst()
                .orElse(target);

        return cartMapper.toDto(result);
    }

    @Transactional(readOnly = true)
    public List<CartLineDto> getCart(Long cartId, String email) {
        return toLines(getOwnedCart(cartId, email));
    }

    // Санын өзгерту (0 болса, кітап себеттен өшеді)
    @Transactional
    public List<CartLineDto> updateQuantity(Long cartId, Long bookId, Integer quantity, String email) {

        if (quantity == null || quantity < 0 || quantity > MAX_QUANTITY) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Quantity must be between 0 and " + MAX_QUANTITY);
        }

        Cart cart = getOwnedCart(cartId, email);
        CartItem item = findItem(cart, bookId);

        if (quantity == 0) {
            cart.getItems().remove(item);
        } else {
            int stock = stockOf(item.getBook());
            // Санын азайтуға әрқашан болады, көбейтуге тек қойма жеткенше
            if (quantity > item.getQuantity() && quantity > stock) {
                throw stock <= 0 ? outOfStock(item.getBook()) : notEnough(item.getBook(), stock);
            }
            item.setQuantity(quantity);
        }

        cartRepository.saveAndFlush(cart);
        return toLines(cart);
    }

    @Transactional
    public List<CartLineDto> removeItem(Long cartId, Long bookId, String email) {
        Cart cart = getOwnedCart(cartId, email);
        CartItem item = findItem(cart, bookId);

        cart.getItems().remove(item);

        cartRepository.saveAndFlush(cart);
        return toLines(cart);
    }

    @Transactional
    public void clearCart(Long cartId, String email) {
        Cart cart = getOwnedCart(cartId, email);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}