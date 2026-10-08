package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Order;
import Book.bitlab.BookStore.Mapper.OrderMapper;
import Book.bitlab.BookStore.dto.OrderDTO;
import Book.bitlab.BookStore.repositories.BookRepository;
import Book.bitlab.BookStore.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private  final OrderMapper orderMapper;

    public OrderDTO create(OrderDTO dto){
        Order order = Order.builder()
                .customerName(dto.getCustomerName())
                .address(dto.getAddress())
                .status("NEW")
                .totalPrice(dto.getTotalPrice())
                .build();
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }
    public List<OrderDTO> getAll(){
        return  orderMapper.toDtoList(
                orderRepository.findAll()
        );
    }
    public  OrderDTO getById(Long id){
        Order order =  orderRepository.findById(id).orElseThrow(()-> new RuntimeException("Oreder not found"));
        return orderMapper.toDto(order);

    }
    public OrderDTO updateStatus(Long id,
                                 String status){
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Order is not found"));
        order.setStatus(status);
        return orderMapper.toDto(orderRepository.save(order));

    }

}
