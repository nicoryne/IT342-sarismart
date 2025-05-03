package me.sarismart.backend.DTO;

import lombok.*;
import me.sarismart.backend.Entity.CartItem;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {
    private Long storeId;
    private List<CartItem> cartItems;
    private double totalPrice;
    private int totalItems;
    private String cartName;
}