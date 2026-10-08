package Book.bitlab.BookStore.Mapper;

import Book.bitlab.BookStore.Entity.Order;
import Book.bitlab.BookStore.dto.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDto(Order order);
    Order toEntity(OrderDTO dto);
    List<OrderDTO> toDtoList(List<Order> orders);
}