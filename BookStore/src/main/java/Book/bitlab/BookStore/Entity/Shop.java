package Book.bitlab.BookStore.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Сатушының кітап дүкені. Бір пайдаланушыда бір дүкен.
@Entity
@Table(name = "shops")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private User owner;

    private LocalDateTime createdAt;
}