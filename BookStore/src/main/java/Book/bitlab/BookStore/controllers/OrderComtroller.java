package Book.bitlab.BookStore.controllers;

import Book.bitlab.BookStore.dto.OrderDTO;
import Book.bitlab.BookStore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")   // "/api/orsers" емес
@RequiredArgsConstructor
public class OrderComtroller {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO dto){
        return ResponseEntity.ok(orderService.create(dto));

    }
    @GetMapping
    public  ResponseEntity<List<OrderDTO>> getAll(){
        return ResponseEntity.ok(orderService.getAll());

    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getByID(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getById(id));
    }
    @PostMapping("/{id}/status")
    public ResponseEntity<@NonNull OrderDTO> updateStatus(@PathVariable Long id,
                                                          @RequestParam String status){
        return ResponseEntity.ok(orderService.updateStatus(id,status));
    }
}
