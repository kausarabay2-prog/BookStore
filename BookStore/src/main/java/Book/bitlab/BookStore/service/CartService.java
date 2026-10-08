package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Cart;
import Book.bitlab.BookStore.Item.CartItem;
import Book.bitlab.BookStore.Mapper.CartMapper;
import Book.bitlab.BookStore.dto.CartItemDto;
import Book.bitlab.BookStore.repositories.BookRepository;
import Book.bitlab.BookStore.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final CartMapper cartMapper;

    public Cart createCart(){
        Cart cart = Cart.builder().build();

        return cartRepository.save(cart);

    }

    public CartItemDto addToCart(Long cartId,
                                 Long bookId,
                                 Integer quantity){
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new RuntimeException("Cart not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new RuntimeException("Book not found"));
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .book(book)
                .quantity(quantity)
                .build();
        cart.getItems().add(cartItem);

        cartRepository.save(cart);
        return cartMapper.toDto(cartItem);

    }
    public List<CartItemDto> getCart(Long cartId){
        Cart cart= cartRepository.findById(cartId)
                .orElseThrow(()-> new RuntimeException("Cart not found"));

        return cart.getItems()
                .stream()
                .map(cartMapper::toDto)
                .toList();
    }
    public void clearCart(Long cartId){
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new RuntimeException("Cart not found"));

        cart.getItems().clear();

        cartRepository.save(cart);
    }
}
