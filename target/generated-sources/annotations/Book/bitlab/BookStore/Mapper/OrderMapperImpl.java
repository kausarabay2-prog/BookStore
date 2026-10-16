package Book.bitlab.BookStore.Mapper;

import Book.bitlab.BookStore.Entity.Order;
import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.dto.OrderDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-15T21:59:55+0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Microsoft)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderDTO toDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDTO.OrderDTOBuilder orderDTO = OrderDTO.builder();

        orderDTO.userEmail( orderUserEmail( order ) );
        orderDTO.id( order.getId() );
        orderDTO.customerName( order.getCustomerName() );
        orderDTO.address( order.getAddress() );
        orderDTO.cartId( order.getCartId() );
        orderDTO.totalPrice( order.getTotalPrice() );
        orderDTO.status( order.getStatus() );
        orderDTO.createdAt( order.getCreatedAt() );

        return orderDTO.build();
    }

    @Override
    public Order toEntity(OrderDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Order.OrderBuilder order = Order.builder();

        order.customerName( dto.getCustomerName() );
        order.address( dto.getAddress() );
        order.cartId( dto.getCartId() );
        order.createdAt( dto.getCreatedAt() );

        return order.build();
    }

    @Override
    public List<OrderDTO> toDtoList(List<Order> orders) {
        if ( orders == null ) {
            return null;
        }

        List<OrderDTO> list = new ArrayList<OrderDTO>( orders.size() );
        for ( Order order : orders ) {
            list.add( toDto( order ) );
        }

        return list;
    }

    private String orderUserEmail(Order order) {
        if ( order == null ) {
            return null;
        }
        User user = order.getUser();
        if ( user == null ) {
            return null;
        }
        String email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }
}
