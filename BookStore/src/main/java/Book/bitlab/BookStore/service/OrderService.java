package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Cart;
import Book.bitlab.BookStore.Entity.Order;
import Book.bitlab.BookStore.Item.CartItem;
import Book.bitlab.BookStore.Item.OrderItem;
import Book.bitlab.BookStore.Mapper.OrderMapper;
import Book.bitlab.BookStore.dto.OrderDTO;
import Book.bitlab.BookStore.repositories.CartRepository;
import Book.bitlab.BookStore.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final List<String> STATUSES =
            List.of("NEW", "PROCESSING", "SHIPPED", "COMPLETED", "CANCELLED");

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDTO create(OrderDTO dto, String email) {

        if (dto == null || dto.getCartId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart ID is required");
        }
        if (dto.getCustomerName() == null || dto.getCustomerName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer name is required");
        }
        if (dto.getAddress() == null || dto.getAddress().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address is required");
        }

        // Тек өз себетіне рұқсат (басқаның cartId-ін жіберуге болмайды)
        Cart cart = cartRepository.findByIdAndUser_Email(dto.getCartId(), email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "You do not have access to this cart"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Order order = Order.builder()
                .customerName(dto.getCustomerName().trim())
                .address(dto.getAddress().trim())
                .cartId(cart.getId())
                .status("NEW")
                .totalPrice(0.0)
                .items(new ArrayList<>())
                .build();

        // Жалпы соманы тек серверде есептейміз, клиенттің бағасы ескерілмейді
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            if (cartItem == null || cartItem.getBook() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart contains an invalid item");
            }

            Book book = cartItem.getBook();
            Integer quantity = cartItem.getQuantity();
            Double price = book.getPrice();

            if (price == null || !Double.isFinite(price) || price < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid price for book: " + book.getId());
            }
            if (quantity == null || quantity <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid quantity for book: " + book.getId());
            }

            total = total.add(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(quantity)));

            order.getItems().add(
                    OrderItem.builder()
                            .book(book)
                            .quanity(quantity)
                            .price(price)
                            .order(order)
                            .build()
            );
        }

        order.setTotalPrice(total.doubleValue());

        Order saved = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return orderMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAll() {
        return orderMapper.toDtoList(orderRepository.findAll());
    }

    @Transactional(readOnly = true)
    public OrderDTO getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found: " + id));
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDTO updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found: " + id));

        if (status == null || !STATUSES.contains(status.trim().toUpperCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status");
        }

        order.setStatus(status.trim().toUpperCase());
        return orderMapper.toDto(orderRepository.save(order));
    }
}