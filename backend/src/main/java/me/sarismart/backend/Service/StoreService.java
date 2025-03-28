package me.sarismart.backend.Service;

import me.sarismart.backend.Entity.Store;
import me.sarismart.backend.Repository.StoreRepository;
import me.sarismart.backend.Repository.UserRepository;
import me.sarismart.backend.Entity.Product;
import me.sarismart.backend.Entity.Report;
import me.sarismart.backend.Entity.Sale;
import me.sarismart.backend.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Optional<Store> getStoreById(Long id) {
        return storeRepository.findById(id);
    }

    public Store createStore(Store store) {
        return storeRepository.save(store);
    }

    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
    }

    public Store updateStore(Long storeId, Store store) {
        Store existingStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        existingStore.setStoreName(store.getStoreName());
        existingStore.setLocation(store.getLocation());
        return storeRepository.save(existingStore);
    }

    // Next methods needs revisions
    // to be implemented based on the actual logic and requirements
    public void assignWorker(Long storeId, Long workerId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        store.getWorkers().add(worker);
        storeRepository.save(store);
    }

    public void removeWorker(Long storeId, Long workerId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        store.getWorkers().remove(worker);
        storeRepository.save(store);
    }

    public List<User> listWorkers(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        return store.getWorkers();
    }

    public List<Product> listProducts(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        return store.getProducts();
    }

    public Product createProduct(Long storeId, Product product) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        product.setStore(store);
        store.getProducts().add(product);
        storeRepository.save(store);
        return product;
    }

    public void modifyProduct(Long storeId, Long productId, Product product) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        Product existingProduct = store.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        storeRepository.save(store);
    }

    public void deleteProduct(Long storeId, Long productId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        Product product = store.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));
        store.getProducts().remove(product);
        storeRepository.save(store);
    }

    public void adjustStock(Long storeId, Long productId, int quantity) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        Product product = store.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStock(product.getStock() + quantity);
        storeRepository.save(store);
    }

    public void createSale(Long storeId, Sale sale) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        sale.setStore(store);
        store.getSales().add(sale);
        storeRepository.save(store);
    }

    public Sale getSale(Long storeId, Long saleId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        return store.getSales().stream()
                .filter(s -> s.getId().equals(saleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sale not found"));
    }

    public List<Sale> listSales(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        return store.getSales();
    }

    public void refundSale(Long storeId, Long saleId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        Sale sale = store.getSales().stream()
                .filter(s -> s.getId().equals(saleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        store.getSales().remove(sale);
        storeRepository.save(store);
    }

    public List<Product> restockAlert(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        return store.getProducts().stream()
                .filter(product -> product.getStock() < product.getReorderLevel())
                .toList();
    }

    public void setReorderLevel(Long storeId, Long productId, int level) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        Product product = store.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setReorderLevel(level);
        storeRepository.save(store);
    }

    public Report dailySales(Long storeId) {
        // Logic to generate daily sales report
        return new Report("Daily", "Today", 1000.0, 10); // Placeholder
    }

    public Report monthlySales(Long storeId) {
        // Logic to generate monthly sales report
        return new Report("Monthly", "This Month", 30000.0, 300); // Placeholder
    }

    public List<Product> inventoryStatus(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        return store.getProducts();
    }
}