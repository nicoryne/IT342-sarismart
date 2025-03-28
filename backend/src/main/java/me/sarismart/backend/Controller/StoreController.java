package me.sarismart.backend.Controller;

import me.sarismart.backend.Entity.Store;
import me.sarismart.backend.Entity.Product;
import me.sarismart.backend.Entity.Sale;
import me.sarismart.backend.Entity.Report;
import me.sarismart.backend.Entity.User;
import me.sarismart.backend.Service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    // Store Management
    @PostMapping
    public Store createStore(@RequestBody Store store) {
        return storeService.createStore(store);
    }

    @GetMapping("/{storeId}")
    public Optional<Store> getStore(@PathVariable Long storeId) {
        return storeService.getStoreById(storeId);
    }

    @PutMapping("/{storeId}")
    public Store updateStore(@PathVariable Long storeId, @RequestBody Store store) {
        return storeService.updateStore(storeId, store);
    }

    @DeleteMapping("/{storeId}")
    public void deleteStore(@PathVariable Long storeId) {
        storeService.deleteStore(storeId);
    }

    // Worker Management
    @PostMapping("/{storeId}/workers/{workerId}")
    public void assignWorker(@PathVariable Long storeId, @PathVariable Long workerId) {
        storeService.assignWorker(storeId, workerId);
    }

    @DeleteMapping("/{storeId}/workers/{workerId}")
    public void removeWorker(@PathVariable Long storeId, @PathVariable Long workerId) {
        storeService.removeWorker(storeId, workerId);
    }

    @GetMapping("/{storeId}/workers")
    public List<User> listWorkers(@PathVariable Long storeId) {
        return storeService.listWorkers(storeId);
    }

    // Product Management
    @GetMapping("/{storeId}/products")
    public List<Product> listProducts(@PathVariable Long storeId) {
        return storeService.listProducts(storeId);
    }

    @PostMapping("/{storeId}/products")
    public Product createProduct(@PathVariable Long storeId, @RequestBody Product product) {
        return storeService.createProduct(storeId, product);
    }

    @PutMapping("/{storeId}/products/{productId}")
    public Product modifyProduct(@PathVariable Long storeId, @PathVariable Long productId, @RequestBody Product product) {
        return storeService.modifyProduct(storeId, productId, product);
    }

    @DeleteMapping("/{storeId}/products/{productId}")
    public void deleteProduct(@PathVariable Long storeId, @PathVariable Long productId) {
        storeService.deleteProduct(storeId, productId);
    }

    @PatchMapping("/{storeId}/products/{productId}/stock")
    public void adjustStock(@PathVariable Long storeId, @PathVariable Long productId, @RequestParam int quantity) {
        storeService.adjustStock(storeId, productId, quantity);
    }

    // Transaction Management
    @PostMapping("/{storeId}/transactions/sales")
    public void createSale(@PathVariable Long storeId, @RequestBody Sale sale) {
        storeService.createSale(storeId, sale);
    }

    @GetMapping("/{storeId}/transactions/sales/{saleId}")
    public Sale getSale(@PathVariable Long storeId, @PathVariable Long saleId) {
        return storeService.getSale(storeId, saleId);
    }

    @GetMapping("/{storeId}/transactions/sales")
    public List<Sale> listSales(@PathVariable Long storeId) {
        return storeService.listSales(storeId);
    }

    @DeleteMapping("/{storeId}/transactions/sales/{saleId}")
    public void refundSale(@PathVariable Long storeId, @PathVariable Long saleId) {
        storeService.refundSale(storeId, saleId);
    }

    // Inventory Management
    @GetMapping("/{storeId}/inventory/alerts")
    public List<Product> restockAlert(@PathVariable Long storeId) {
        return storeService.restockAlert(storeId);
    }

    @PutMapping("/{storeId}/inventory/{productId}/reorder")
    public void setReorderLevel(@PathVariable Long storeId, @PathVariable Long productId, @RequestParam int level) {
        storeService.setReorderLevel(storeId, productId, level);
    }

    // Reports
    @GetMapping("/{storeId}/reports/daily")
    public Report dailySales(@PathVariable Long storeId) {
        return storeService.dailySales(storeId);
    }

    @GetMapping("/{storeId}/reports/monthly")
    public Report monthlySales(@PathVariable Long storeId) {
        return storeService.monthlySales(storeId);
    }

    @GetMapping("/{storeId}/reports/inventory")
    public List<Product> inventoryStatus(@PathVariable Long storeId) {
        return storeService.inventoryStatus(storeId);
    }
}
