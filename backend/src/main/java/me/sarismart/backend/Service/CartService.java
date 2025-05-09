package me.sarismart.backend.Service;

import me.sarismart.backend.Entity.Cart;
import me.sarismart.backend.Entity.CartItem;
import me.sarismart.backend.Entity.Product;
import me.sarismart.backend.Entity.Store;
import me.sarismart.backend.Entity.User;
import me.sarismart.backend.Repository.CartItemsRepository;
import me.sarismart.backend.Repository.CartRepository;
import me.sarismart.backend.Repository.ProductRepository;
import me.sarismart.backend.Repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;

    @Transactional
    public Cart createCart(Long storeId, List<CartItem> cartItems, double totalPrice, int totalItems, String cartName) {
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("Cart items cannot be null or empty");
            throw new RuntimeException("Cart items cannot be null or empty");
        }

        if (storeId == null) {
            System.out.println("Store ID cannot be null");
            throw new RuntimeException("Store ID cannot be null");
        }

        if (totalPrice <= 0) {
            System.out.println("Total price must be greater than zero");
            throw new RuntimeException("Total price must be greater than zero");
        }

        if (totalItems <= 0) {
            System.out.println("Total items must be greater than zero");
            throw new RuntimeException("Total items must be greater than zero");
        }

        if (cartName == null || cartName.isEmpty()) {
            System.out.println("Cart name cannot be null or empty");
            throw new RuntimeException("Cart name cannot be null or empty");
        }

        String currentUserId = storeService.getCurrentUserId();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        Cart cart = new Cart();
        cart.setCartName(cartName);
        cart.setTotalPrice(totalPrice);
        cart.setTotalItems(totalItems);
        cart.setStore(store);

        User seller = userService.getUserBySupabaseUid(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        cart.setSeller(seller);

        Cart savedCart = cartRepository.save(cart);

        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            double expectedSubtotal = product.getPrice() * item.getQuantity();
            if (Double.compare(expectedSubtotal, item.getSubtotal()) != 0) {
                System.out.println("Invalid subtotal for product: " + product.getName());
                throw new RuntimeException("Invalid subtotal for product: " + product.getName());
            }

            item.setCart(savedCart);
            item.setProduct(product);
            cartItemsRepository.save(item);
        }

        return savedCart;
    }

    public List<Cart> getCartsByStoreId(Long storeId) {
        return cartRepository.findByStoreId(storeId);
    }

    public List<Cart> getCartsByOwnerId(String ownerId) {
        return cartRepository.findByOwnerId(ownerId);
    }

    public List<Cart> getCartsByCartName(String cartName) {
        return cartRepository.findByCartNameContainingIgnoreCase(cartName);
    }
}