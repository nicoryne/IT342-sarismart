package me.sarismart.backend.Controller;

import me.sarismart.backend.DTO.CartRequest;
import me.sarismart.backend.Entity.Cart;
import me.sarismart.backend.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public Cart createCart(@RequestBody CartRequest cartRequest) {
        return cartService.createCart(
                cartRequest.getStoreId(),
                cartRequest.getCartItems(),
                cartRequest.getTotalPrice(),
                cartRequest.getTotalItems(),
                cartRequest.getCartName()
        );
    }

    @GetMapping("/store/{storeId}")
    public List<Cart> getCartsByStoreId(@PathVariable Long storeId) {
        return cartService.getCartsByStoreId(storeId);
    }

    @GetMapping("/owner/{ownerId}")
    public List<Cart> getCartsByOwnerId(@PathVariable String ownerId) {
        return cartService.getCartsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<Cart> getCartsByCartName(@RequestParam String cartName) {
        return cartService.getCartsByCartName(cartName);
    }
}