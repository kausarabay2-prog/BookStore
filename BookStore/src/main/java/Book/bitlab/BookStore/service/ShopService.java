package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.Shop;
import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.dto.ShopDTO;
import Book.bitlab.BookStore.dto.ShopRequest;
import Book.bitlab.BookStore.repositories.ShopRepository;
import Book.bitlab.BookStore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private static final String SELLER = "ROLE_SELLER";
    private static final String ADMIN = "ROLE_ADMIN";

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    private ShopDTO toDto(Shop shop) {
        return new ShopDTO(
                shop.getId(),
                shop.getName(),
                shop.getDescription(),
                shop.getOwner() == null ? null : shop.getOwner().getUsername()
        );
    }

    private ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
//    Метод проверяет и очищает название магазина.
    private String cleanName(ShopRequest req) {
        if (req == null || req.name() == null || req.name().isBlank()) {
            throw bad("Shop name is required");
        }
        String name = req.name().trim();
        if (name.length() < 2 || name.length() > 60) {
            throw bad("Shop name must be 2 to 60 characters");
        }
        return name;
    }
//проверка описания
    private String cleanDescription(ShopRequest req) {
        if (req.description() == null) return null;
        String d = req.description().trim();
        if (d.length() > 500) {
            throw bad("Description must be at most 500 characters");
        }
        return d.isEmpty() ? null : d;
    }

    // Дүкенді тек қызметкер аша алады: админ ROLE_SELLER берген адам (немесе админ).
    // Қарапайым пайдаланушы өзін сатушы ете алмайды.
    @Transactional
    public ShopDTO open(String email, ShopRequest req) {
        String name = cleanName(req);
        String description = cleanDescription(req);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"));
//Проверка роли
        boolean staff = user.getRoles().stream()
                .anyMatch(r -> SELLER.equals(r.getName()) || ADMIN.equals(r.getName()));
        if (!staff) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only staff members approved by an admin can open a shop");
        }

        if (shopRepository.findByOwner_Email(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have a shop");
        }

        if (shopRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A shop with this name already exists");
        }

        Shop shop = shopRepository.save(Shop.builder()
                .name(name)
                .description(description)
                .owner(user)
                .createdAt(LocalDateTime.now())
                .build());

        return toDto(shop);
    }

    @Transactional
    public ShopDTO updateMine(String email, ShopRequest req) {
        Shop shop = shopRepository.findByOwner_Email(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "You do not have a shop"));

        String name = cleanName(req);
        if (shopRepository.existsByNameIgnoreCaseAndIdNot(name, shop.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A shop with this name already exists");
        }

        shop.setName(name);
        shop.setDescription(cleanDescription(req));
        return toDto(shopRepository.save(shop));
    }
//получение своего магазина
    @Transactional(readOnly = true)
    public ShopDTO getMine(String email) {
        return shopRepository.findByOwner_Email(email)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "You do not have a shop"));
    }

    @Transactional(readOnly = true)
    public ShopDTO getById(Long id) {
        return shopRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Shop not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ShopDTO> getAll() {
        return shopRepository.findAllByOrderByNameAsc().stream().map(this::toDto).toList();
    }
}