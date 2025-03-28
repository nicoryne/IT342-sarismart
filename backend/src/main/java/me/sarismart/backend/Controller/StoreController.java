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

    // REST Resource: v1.stores
    // Method: stores.create
    @PostMapping
    public Store createStore(@RequestBody Store store) {
        return storeService.createStore(store);
    }

    // Method: stores.get
    @GetMapping("/{storeId}")
    public Optional<Store> getStore(@PathVariable Long storeId) {
        return storeService.getStoreById(storeId);
    }

    // Method: stores.update
    @PutMapping("/{storeId}")
    public Store updateStore(@PathVariable Long storeId, @RequestBody Store store) {
        return storeService.updateStore(storeId, store);
    }

    // Method: stores.delete
    @DeleteMapping("/{storeId}")
    public void deleteStore(@PathVariable Long storeId) {
        storeService.deleteStore(storeId);
    }

    // REST Resource: stores.workers
    // Method: stores.workers.assign
    @PostMapping("/{storeId}/workers/{workerId}")
    public void assignWorker(@PathVariable Long storeId, @PathVariable Long workerId) {
        storeService.assignWorker(storeId, workerId);
    }

    // Method: stores.workers.delete
    @DeleteMapping("/{storeId}/workers/{workerId}")
    public void removeWorker(@PathVariable Long storeId, @PathVariable Long workerId) {
        storeService.removeWorker(storeId, workerId);
    }

    // Method: stores.workers.list
    @GetMapping("/{storeId}/workers")
    public List<User> listWorkers(@PathVariable Long storeId) {
        return storeService.listWorkers(storeId);
    }

    // REST Resource: stores.products
    // Method: stores.products.list
    @GetMapping("/{storeId}/products")
    public List<Product> listProducts(@PathVariable Long storeId) {
        return storeService.listProducts(storeId);
    }

    // Method: stores.products.create
    @PostMapping("/{storeId}/products")
    public Product createProduct(@PathVariable Long storeId, @RequestBody Product product) {
        return storeService.createProduct(storeId, product);
    }

    // Method: stores.products.modify
    @PutMapping("/{storeId}/products/{productId}")
    public Product modifyProduct(@PathVariable Long storeId, @PathVariable Long productId, @RequestBody Product product) {
        return storeService.modifyProduct(storeId, productId, product);
    }

    // Method: stores.products.delete
    @DeleteMapping("/{storeId}/products/{productId}")
    public void deleteProduct(@PathVariable Long storeId, @PathVariable Long productId) {
        storeService.deleteProduct(storeId, productId);
    }

    // Method: stores.products.adjustStock
    @PatchMapping("/{storeId}/products/{productId}/stock")
    public void adjustStock(@PathVariable Long storeId, @PathVariable Long productId, @RequestParam int quantity) {
        storeService.adjustStock(storeId, productId, quantity);
    }

    // REST Resource: stores.transactions
    // Method: stores.transactions.createSale
    @PostMapping("/{storeId}/transactions/sales")
    public void createSale(@PathVariable Long storeId, @RequestBody Sale sale) {
        storeService.createSale(storeId, sale);
    }

    // Method: stores.transactions.getSale
    @GetMapping("/{storeId}/transactions/sales/{saleId}")
    public Sale getSale(@PathVariable Long storeId, @PathVariable Long saleId) {
        return storeService.getSale(storeId, saleId);
    }

    // Method: stores.transactions.listSales
    @GetMapping("/{storeId}/transactions/sales")
    public List<Sale> listSales(@PathVariable Long storeId) {
        return storeService.listSales(storeId);
    }

    // Method: stores.transactions.refundSale
    @DeleteMapping("/{storeId}/transactions/sales/{saleId}")
    public void refundSale(@PathVariable Long storeId, @PathVariable Long saleId) {
        storeService.refundSale(storeId, saleId);
    }

    // REST Resource: stores.restockInventory
    // Method: stores.inventory.restockAlert
    @GetMapping("/{storeId}/inventory/alerts")
    public List<Product> restockAlert(@PathVariable Long storeId) {
        return storeService.restockAlert(storeId);
    }

    // Method: stores.inventory.setReorderLevel
    @PutMapping("/{storeId}/inventory/{productId}/reorder")
    public void setReorderLevel(@PathVariable Long storeId, @PathVariable Long productId, @RequestParam int level) {
        storeService.setReorderLevel(storeId, productId, level);
    }

    // REST Resource: stores.reports
    // Method: stores.reports.dailySales
    @GetMapping("/{storeId}/reports/daily")
    public Report dailySales(@PathVariable Long storeId) {
        return storeService.dailySales(storeId);
    }

    // Method: stores.reports.monthlySales
    @GetMapping("/{storeId}/reports/monthly")
    public Report monthlySales(@PathVariable Long storeId) {
        return storeService.monthlySales(storeId);
    }

    // Method: stores.reports.monthlySales
    @GetMapping("/{storeId}/reports/inventory")
    public List<Product> inventoryStatus(@PathVariable Long storeId) {
        return storeService.inventoryStatus(storeId);
    }
}
