package Book.bitlab.BookStore.dto;

import java.util.List;

// Админ панеліндегі пайдаланушы тізімі (пароль ешқашан жіберілмейді)
public record AdminUserDto(
        Long id,
        String username,
        String email,
        List<String> roles
) {}