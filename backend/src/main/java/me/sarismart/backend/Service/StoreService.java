package me.sarismart.backend.Service;

import me.sarismart.backend.Entity.Store;
import me.sarismart.backend.Repository.SaleRepository;
import me.sarismart.backend.Repository.StoreRepository;
import me.sarismart.backend.Repository.UserRepository;
import me.sarismart.backend.Repository.StockAdjustmentRepository;
import me.sarismart.backend.Security.JwtUtil;
import me.sarismart.backend.DTO.StoreRequest;
import me.sarismart.backend.Entity.Product;
import me.sarismart.backend.Entity.Report;
import me.sarismart.backend.Entity.Sale;
import me.sarismart.backend.Entity.StockAdjustment;
import me.sarismart.backend.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StoreService {
        @Autowired
        private StoreRepository storeRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private SaleRepository saleRepository;

        @Autowired
        private StockAdjustmentRepository stockAdjustmentRepository;

        @Autowired
        private JwtUtil jwtUtil;
        
        @Autowired
        private AuthorizationService authorizationService;

        private String getCurrentUserId() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()) {
                        throw new RuntimeException("User is not authenticated");
                }
                
                Object credentials = authentication.getCredentials();
                if (!(credentials instanceof String token)) {
                        throw new RuntimeException("Invalid authentication token");
                }
                
                try {
                        return jwtUtil.getUserIdFromToken(token);
                } catch (Exception e) {
                        throw new RuntimeException("Failed to extract user ID from token", e);
                }
        }

        private void authorizeOwner(Store store) {
                String currentUserId = getCurrentUserId();
                authorizationService.authorizeOwner(store, currentUserId);
        }
            
        private void authorizeOwnerOrWorker(Store store) {
                String currentUserId = getCurrentUserId();
                authorizationService.authorizeOwnerOrWorker(store, currentUserId);
        }

        public List<Store> getAllStores() {
                return storeRepository.findAll();
        }

        public Optional<Store> getStoreById(Long id) {
                return storeRepository.findById(id);
        }

        public List<Store> getStoresByOwnerId(String ownerId) {
                return storeRepository.findByOwner_SupabaseUid(ownerId);
        }

        public List<Store> getStoresByWorkerId(String workerId) {
                return storeRepository.findByWorkers_SupabaseUid(workerId);
        }

        public Store createStore(StoreRequest store) {
                User owner = userRepository.findBySupabaseUid(store.getOwnerId())
                        .orElseThrow(() -> new RuntimeException("Owner not found"));
                
                Store newStore = new Store();
                newStore.setStoreName(store.getStoreName());
                newStore.setLocation(store.getLocation());
                newStore.setLatitude(store.getLatitude());
                newStore.setLongitude(store.getLongitude());
                newStore.setOwner(owner);

                storeRepository.save(newStore);
                return newStore;
        }

        public void deleteStore(Long storeId) {
                Store existingStore = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));
            
                authorizeOwner(existingStore);
            
                storeRepository.delete(existingStore);
        }

        public Store updateStore(Long storeId, Store updatedStore) {
                Store existingStore = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));
            
                authorizeOwner(existingStore);
            
                existingStore.setStoreName(updatedStore.getStoreName());
                existingStore.setLocation(updatedStore.getLocation());
                existingStore.setLatitude(updatedStore.getLatitude());
                existingStore.setLongitude(updatedStore.getLongitude());
            
                return storeRepository.save(existingStore);
        }

        public void assignWorker(Long storeId, Long workerId) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));
                User worker = userRepository.findById(workerId)
                        .orElseThrow(() -> new RuntimeException("Worker not found"));

                authorizeOwner(store);

                store.getWorkers().add(worker);
                storeRepository.save(store);
        }

        public void removeWorker(Long storeId, Long workerId) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));
                User worker = userRepository.findById(workerId)
                        .orElseThrow(() -> new RuntimeException("Worker not found"));

                authorizeOwner(store);

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
                
                authorizeOwner(store);
                
                product.setStore(store);
                store.getProducts().add(product);
                storeRepository.save(store);
                return product;
        }

        public Product modifyProduct(Long storeId, Long productId, Product product) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));
                Product existingProduct = store.getProducts().stream()
                        .filter(p -> p.getId().equals(productId))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                authorizeOwnerOrWorker(store);

                existingProduct.setName(product.getName());
                existingProduct.setCategory(product.getCategory());
                existingProduct.setDescription(product.getDescription());
                storeRepository.save(store);
                return existingProduct;
        }

        public Product modifyProductByOwner(Long storeId, Long productId, Product product) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));
                Product existingProduct = store.getProducts().stream()
                        .filter(p -> p.getId().equals(productId))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                authorizeOwner(store);
                
                existingProduct.setBarcode(product.getBarcode());
                existingProduct.setName(product.getName());
                existingProduct.setCategory(product.getCategory());
                existingProduct.setDescription(product.getDescription());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setStock(product.getStock());
                existingProduct.setReorderLevel(product.getReorderLevel());
                
                storeRepository.save(store);
                return existingProduct;
        }

        public void deleteProduct(Long storeId, Long productId) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));
                Product product = store.getProducts().stream()
                        .filter(p -> p.getId().equals(productId))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                
                authorizeOwner(store);

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

                authorizeOwnerOrWorker(store);

                String currentUserId = getCurrentUserId();
                User user = userRepository.findBySupabaseUid(currentUserId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                int oldStock = product.getStock();
                int newStock = oldStock + quantity;

                StockAdjustment adjustment = new StockAdjustment();
                adjustment.setStore(store);
                adjustment.setUser(user);
                adjustment.setProduct(product);
                adjustment.setOldStock(oldStock);
                adjustment.setNewStock(newStock);
                adjustment.setTimestamp(LocalDateTime.now());
                stockAdjustmentRepository.save(adjustment);

                product.setStock(newStock);
                storeRepository.save(store);
        }

        public List<StockAdjustment> listStockAdjustmentsByStore(Long storeId) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));

                authorizeOwnerOrWorker(store);

                return stockAdjustmentRepository.findByStoreId(storeId);
        }

        public List<StockAdjustment> listStockAdjustmentsByProduct(Long storeId, Long productId) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));

                authorizeOwnerOrWorker(store);

                return stockAdjustmentRepository.findByStoreIdAndProductId(storeId, productId);
        }

        public void createSale(Long storeId, Sale sale) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));

                authorizeOwnerOrWorker(store);

                sale.setStore(store);
                store.getSales().add(sale);
                storeRepository.save(store);
        }

        public Sale getSale(Long storeId, Long saleId) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));

                authorizeOwnerOrWorker(store);

                return store.getSales().stream()
                        .filter(s -> s.getId().equals(saleId))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Sale not found"));
        }

        public List<Sale> listSales(Long storeId) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));
                
                authorizeOwnerOrWorker(store);

                return store.getSales();
        }

        public void refundSale(Long storeId, Long saleId) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));
                Sale sale = store.getSales().stream()
                        .filter(s -> s.getId().equals(saleId))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Sale not found"));

                authorizeOwnerOrWorker(store);

                store.getSales().remove(sale);
                storeRepository.save(store);
        }

        public List<Product> restockAlert(Long storeId) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));

                authorizeOwnerOrWorker(store);

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

                authorizeOwnerOrWorker(store);

                product.setReorderLevel(level);
                storeRepository.save(store);
        }

        public Report dailySales(Long storeId) {
                LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = startOfDay.plusDays(1);

                List<Sale> todaySales = saleRepository.findByStoreIdAndSaleDateBetween(storeId, startOfDay, endOfDay);

                double totalAmount = todaySales.stream()
                        .mapToDouble(Sale::getTotalAmount)
                        .sum();
                int numberOfSales = todaySales.size();

                return new Report("Daily", "Today", totalAmount, numberOfSales);
        }

        public Report monthlySales(Long storeId) {
                LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
                LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

                List<Sale> monthlySales = saleRepository.findByStoreIdAndSaleDateBetween(storeId, startOfMonth, endOfMonth);

                double totalAmount = monthlySales.stream()
                        .mapToDouble(Sale::getTotalAmount)
                        .sum();
                int numberOfSales = monthlySales.size();

                return new Report("Monthly", "This Month", totalAmount, numberOfSales);
        }

        public List<Product> inventoryStatus(Long storeId) {
                Store store = storeRepository.findById(storeId)
                        .orElseThrow(() -> new RuntimeException("Store not found"));
                return store.getProducts();
        }
}