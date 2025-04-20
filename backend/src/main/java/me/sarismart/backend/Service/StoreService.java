package me.sarismart.backend.Service;

import me.sarismart.backend.Entity.Store;
import me.sarismart.backend.Repository.SaleRepository;
import me.sarismart.backend.Repository.StoreRepository;
import me.sarismart.backend.Repository.UserRepository;
import me.sarismart.backend.DTO.StoreRequest;
import me.sarismart.backend.Entity.Product;
import me.sarismart.backend.Entity.Report;
import me.sarismart.backend.Entity.Sale;
import me.sarismart.backend.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
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

        public List<Store> getAllStores() {
                return storeRepository.findAll();
        }

        public Optional<Store> getStoreById(Long id) {
                return storeRepository.findById(id);
        }

        public List<Store> getStoresByOwnerId(Long ownerId) {
                return storeRepository.findByOwnerId(ownerId);
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

        public Product modifyProduct(Long storeId, Long productId, Product product) {
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
                return existingProduct;
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