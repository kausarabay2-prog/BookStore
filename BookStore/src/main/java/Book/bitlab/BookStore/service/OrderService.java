package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Book;
import Book.bitlab.BookStore.Entity.Cart;
import Book.bitlab.BookStore.Entity.Order;
import Book.bitlab.BookStore.Item.CartItem;
import Book.bitlab.BookStore.Item.OrderItem;
import Book.bitlab.BookStore.Mapper.OrderMapper;
import Book.bitlab.BookStore.dto.OrderDTO;
import Book.bitlab.BookStore.repositories.BookRepository;
import Book.bitlab.BookStore.repositories.CartRepository;
import Book.bitlab.BookStore.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final List<String> STATUSES =
            List.of("NEW", "PROCESSING", "SHIPPED", "COMPLETED", "CANCELLED");

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
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

        // Тек өз себетіне рұқсат (админ мен сатушы да өз себетінен кез келген кітап,
        // оның ішінде өз кітабын да сатып ала алады)
        Cart cart = cartRepository.findByIdAndUser_Email(dto.getCartId(), email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "You do not have access to this cart"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        // Әр кітап бойынша жалпы санды жинаймыз
        Map<Long, Integer> wanted = new LinkedHashMap<>();
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem == null || cartItem.getBook() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart contains an invalid item");
            }
            Integer quantity = cartItem.getQuantity();
            if (quantity == null || quantity <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid quantity for book: " + cartItem.getBook().getId());
            }
            wanted.merge(cartItem.getBook().getId(), quantity, Integer::sum);
        }

        // Қойма жолдарын қамаймыз: екі адам бір мезгілде соңғы данаға тапсырыс бере алмайды
        Map<Long, Book> locked = new HashMap<>();
        for (Book b : bookRepository.findAllByIdForUpdate(wanted.keySet())) {
            locked.put(b.getId(), b);
        }

        Order order = Order.builder()
                .customerName(dto.getCustomerName().trim())
                .address(dto.getAddress().trim())
                .cartId(cart.getId()).user(cart.getUser())
                .createdAt(LocalDateTime.now())
                .status("NEW")
                .totalPrice(0.0)
                .items(new ArrayList<>())
                .build();

        // Жалпы соманы тек серверде есептейміз, клиенттің бағасы ескерілмейді
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : wanted.entrySet()) {
            Book book = locked.get(entry.getKey());
            int quantity = entry.getValue();

            if (book == null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Oops! A book in your basket no longer exists. Please remove it.");
            }

            int stock = book.getStock() == null ? 0 : book.getStock();
            if (stock <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Oops! \"" + book.getTitle() + "\" is out of stock. Please remove it from your basket.");
            }
            if (quantity > stock) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Oops! Only " + stock + " cop" + (stock == 1 ? "y" : "ies")
                                + " of \"" + book.getTitle() + "\" left in stock.");
            }

            Double price = book.getPrice();
            if (price == null || !Double.isFinite(price) || price < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid price for book: " + book.getId());
            }

            total = total.add(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(quantity)));

            // Қоймадан азайту
            book.setStock(stock - quantity);

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

    // Пайдаланушының өз тапсырыстары
    @Transactional(readOnly = true)
    public List<OrderDTO> getMine(String email) {
        return orderMapper.toDtoList(orderRepository.findByUser_EmailOrderByIdDesc(email));
    }

    // ADMIN: барлық тапсырыс (жаңасы бірінші)
    @Transactional(readOnly = true)
    public List<OrderDTO> getAll() {
        return orderMapper.toDtoList(
                orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
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

        String newStatus = status.trim().toUpperCase();

        // Тапсырыс бас тартылса, кітаптар қоймаға қайтарылады (бір рет қана)
        if ("CANCELLED".equals(newStatus) && !"CANCELLED".equals(order.getStatus())
                && order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Book book = item.getBook();
                Integer qty = item.getQuanity();
                if (book != null && qty != null) {
                    int current = book.getStock() == null ? 0 : book.getStock();
                    book.setStock(current + qty);
                }
            }
        }

        order.setStatus(newStatus);
        return orderMapper.toDto(orderRepository.save(order));
    }
}