package me.sarismart.backend.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;
    
    @Column(nullable = false)
    private double price;

    private int stock;

    private String description;

    private int reorderLevel;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}
