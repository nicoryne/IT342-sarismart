package me.sarismart.backend.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cartName;

    @Column(nullable = false)
    private double totalPrice;

    @Column(nullable = false)
    private int totalItems;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}