package Book.bitlab.BookStore.Mapper;

import Book.bitlab.BookStore.Entity.Order;
import Book.bitlab.BookStore.dto.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDTO toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "status", ignore = true)
    Order toEntity(OrderDTO dto);

    List<OrderDTO> toDtoList(List<Order> orders);
}
