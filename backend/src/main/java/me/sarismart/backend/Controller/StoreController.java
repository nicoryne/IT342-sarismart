package me.sarismart.backend.Controller;

import me.sarismart.backend.Entity.Store;
import me.sarismart.backend.DTO.StoreRequest;
import me.sarismart.backend.Entity.Product;
import me.sarismart.backend.Entity.Sale;
import me.sarismart.backend.Entity.StockAdjustment;
import me.sarismart.backend.Entity.Report;
import me.sarismart.backend.Entity.User;
import me.sarismart.backend.Service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "StoreController", description = "Store Management")
public class StoreController {

    @Autowired
    private StoreService storeService;

    // REST Resource: v1.stores
    // Method: stores.create
    // Must be authenticated to create a store
    @Operation(summary = "Create Store", description = "Create a new store")
    @PostMapping
    public Store createStore(@RequestBody StoreRequest store) {
        return storeService.createStore(store);
    }

    // Method: stores.getNearby
    // Permit all users to get nearby stores
    @Operation(summary = "Get Nearby Stores", description = "Retrieve a list of nearby stores")
    @GetMapping("/nearby/{latitude}/{longitude}/{radius}")
    public List<Store> getNearbyStores(@PathVariable double latitude, @PathVariable double longitude, @PathVariable double radius) {
        return storeService.getNearbyStores(latitude, longitude, radius);
    }

    // Method: stores.get
    // Permit all users to get a store by ID
    @Operation(summary = "Get Store by ID", description = "Retrieve a store by its ID")
    @GetMapping("/{storeId}")
    public Optional<Store> getStore(@PathVariable Long storeId) {
        return storeService.getStoreById(storeId);
    }

    // Method: stores.getAll
    // Permit all users to get a list of all stores
    @Operation(summary = "Get All Stores", description = "Retrieve a list of all stores")
    @GetMapping
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }

    // Method: stores.getStoresByOwnerId
    // Must be authenticated to get stores by owner ID
    @Operation(summary = "Get Stores by Owner ID", description = "Retrieve stores owned by a specific user")
    @GetMapping("/owner/{ownerId}")
    public List<Store> getStoresByOwnerId(@PathVariable String ownerId) {
        return storeService.getStoresByOwnerId(ownerId);
    }

    // Method: stores.getStoresByWorkerId
    // Must be authenticated to get stores by worker ID
    @Operation(summary = "Get Stores by Worker ID", description = "Retrieve stores where a specific user is a worker")
    @GetMapping("/worker/{workerId}")
    public List<Store> getStoresByWorkerId(@PathVariable String workerId) {
        return storeService.getStoresByWorkerId(workerId);
    }

    // Method: stores.update
    // Must be authenticated to update a store
    @Operation(summary = "Update Store", description = "Update an existing store")
    @PutMapping("/{storeId}")
    public Store updateStore(@PathVariable Long storeId, @RequestBody Store store) {
        return storeService.updateStore(storeId, store);
    }

    // Method: stores.delete
    // Must be authenticated to delete a store
    @Operation(summary = "Delete Store", description = "Delete a store by its ID")
    @DeleteMapping("/{storeId}")
    public void deleteStore(@PathVariable Long storeId) {
        storeService.deleteStore(storeId);
    }

    // REST Resource: stores.workers
    // Method: stores.workers.assign
    // Must be authenticated to assign a worker to a store
    @Operation(summary = "Assign Worker to Store", description = "Assign a worker to a store")
    @PostMapping("/{storeId}/workers/{workerId}")
    public void assignWorker(@PathVariable Long storeId, @PathVariable Long workerId) {
        storeService.assignWorker(storeId, workerId);
    }

    // Method: stores.workers.delete
    // Must be authenticated to remove a worker from a store
    @Operation(summary = "Remove Worker from Store", description = "Remove a worker from a store")
    @DeleteMapping("/{storeId}/workers/{workerId}")
    public void removeWorker(@PathVariable Long storeId, @PathVariable Long workerId) {
        storeService.removeWorker(storeId, workerId);
    }

    // Method: stores.workers.list
    // Permit all users to get a list of workers in a store
    @Operation(summary = "List Workers in Store", description = "Retrieve a list of workers assigned to a store")
    @GetMapping("/{storeId}/workers")
    public List<User> listWorkers(@PathVariable Long storeId) {
        return storeService.listWorkers(storeId);
    }

    // REST Resource: stores.products
    // Method: stores.products.list
    // Permit all users to get a list of products in a store
    @Operation(summary = "List Products in Store", description = "Retrieve a list of products available in a store")
    @GetMapping("/{storeId}/products")
    public List<Product> listProducts(@PathVariable Long storeId) {
        return storeService.listProducts(storeId);
    }

    // Method: stores.products.create
    // Must be authenticated to create a product in a store
    @Operation(summary = "Create Product in Store", description = "Create a new product in a store")
    @PostMapping("/{storeId}/products")
    public Product createProduct(@PathVariable Long storeId, @RequestBody Product product) {
        return storeService.createProduct(storeId, product);
    }

    // Method: stores.products.modify
    // Must be authenticated to modify a product in a store
    @Operation(summary = "Modify General Product Information in Store", description = "Modify an existing product in a store")
    @PutMapping("/{storeId}/products/{productId}")
    public Product modifyProduct(@PathVariable Long storeId, @PathVariable Long productId, @RequestBody Product product) {
        return storeService.modifyProduct(storeId, productId, product);
    }

    //Method: stores.owner.products.modify
    // Must be authenticated to modify a product in a store by owner
    @Operation(summary = "Modify Product in Store by Owner", description = "Modify an existing product in a store by owner")
    @PutMapping("/{storeId}/owner/products/{productId}")
    public Product modifyProductByOwner(@PathVariable Long storeId, @PathVariable Long productId, @RequestBody Product product) {
        return storeService.modifyProductByOwner(storeId, productId, product);
    }

    // Method: stores.products.delete
    // Must be authenticated to delete a product from a store
    @Operation(summary = "Delete Product from Store", description = "Delete a product from a store")
    @DeleteMapping("/{storeId}/products/{productId}")
    public void deleteProduct(@PathVariable Long storeId, @PathVariable Long productId) {
        storeService.deleteProduct(storeId, productId);
    }

    // Method: stores.products.adjustStock
    // Must be authenticated to adjust the stock of a product in a store
    @Operation(summary = "Adjust Product Stock", description = "Adjust the stock of a product in a store")
    @PatchMapping("/{storeId}/products/{productId}/stock")
    public void adjustStock(@PathVariable Long storeId, @PathVariable Long productId, @RequestParam int quantity) {
        storeService.adjustStock(storeId, productId, quantity);
    }

    // Method: stores.products.getStockAdjustmentHistoryPerStore
    // Must be authenticated to get the stock adjustment history of a store
    @Operation(summary = "Get Stock Adjustment History of Store", description = "Retrieve the stock adjustment history of a store")
    @GetMapping("/{storeId}/stock/history")
    public List<StockAdjustment> listStockAdjustmentsByStore(@PathVariable Long storeId) {
        return storeService.listStockAdjustmentsByStore(storeId);
    }

    // Method: stores.products.getStockAdjustmentHistoryPerProduct
    // Must be authenticated to get the stock adjustment history of a product in a store
    @Operation(summary = "Get Stock Adjustment History of a Product", description = "Retrieve the stock adjustment history of a product in a store")
    @GetMapping("/{storeId}/products/{productId}/stock/history")
    public List<StockAdjustment> listStockAdjustmentsByProduct(@PathVariable Long storeId, @PathVariable Long productId) {
        return storeService.listStockAdjustmentsByProduct(storeId, productId);
    }

    // REST Resource: stores.transactions
    // Method: stores.transactions.createSale
    // Must be authenticated to create a sale transaction in a store
    @Operation(summary = "Create Sale Transaction", description = "Create a new sale transaction in a store")
    @PostMapping("/{storeId}/transactions/sales")
    public void createSale(@PathVariable Long storeId, @RequestBody Sale sale) {
        storeService.createSale(storeId, sale);
    }

    // Method: stores.transactions.getSale
    // Must be authenticated to get a sale transaction by ID
    @Operation(summary = "Get Sale Transaction by ID", description = "Retrieve a sale transaction by its ID")
    @GetMapping("/{storeId}/transactions/sales/{saleId}")
    public Sale getSale(@PathVariable Long storeId, @PathVariable Long saleId) {
        return storeService.getSale(storeId, saleId);
    }

    // Method: stores.transactions.listSales
    // Must be authenticated to get a list of sale transactions in a store
    @Operation(summary = "List Sale Transactions", description = "Retrieve a list of sale transactions in a store")
    @GetMapping("/{storeId}/transactions/sales")
    public List<Sale> listSales(@PathVariable Long storeId) {
        return storeService.listSales(storeId);
    }

    // Method: stores.transactions.refundSale
    // Must be authenticated to refund a sale transaction in a store
    @Operation(summary = "Refund Sale Transaction", description = "Refund a sale transaction in a store")
    @DeleteMapping("/{storeId}/transactions/sales/{saleId}")
    public void refundSale(@PathVariable Long storeId, @PathVariable Long saleId) {
        storeService.refundSale(storeId, saleId);
    }

    // REST Resource: stores.restockInventory
    // Method: stores.inventory.restockAlert
    // Must be authenticated to get restock alerts for a store
    @Operation(summary = "Get Restock Alerts", description = "Retrieve a list of products that need to be restocked")
    @GetMapping("/{storeId}/inventory/alerts")
    public List<Product> restockAlert(@PathVariable Long storeId) {
        return storeService.restockAlert(storeId);
    }

    // Method: stores.inventory.setReorderLevel
    // Must be authenticated to set the reorder level for a product in a store
    @Operation(summary = "Set Reorder Level", description = "Set the reorder level for a product in a store")
    @PutMapping("/{storeId}/inventory/{productId}/reorder")
    public void setReorderLevel(@PathVariable Long storeId, @PathVariable Long productId, @RequestParam int level) {
        storeService.setReorderLevel(storeId, productId, level);
    }

    // REST Resource: stores.reports
    // Method: stores.reports.dailySales
    // Must be authenticated to get the daily sales report for a store
    @Operation(summary = "Get Daily Sales Report", description = "Retrieve the daily sales report for a store")
    @GetMapping("/{storeId}/reports/daily")
    public Report dailySales(@PathVariable Long storeId) {
        return storeService.dailySales(storeId);
    }

    // Method: stores.reports.monthlySales
    // Must be authenticated to get the monthly sales report for a store
    @Operation(summary = "Get Monthly Sales Report", description = "Retrieve the monthly sales report for a store")
    @GetMapping("/{storeId}/reports/monthly")
    public Report monthlySales(@PathVariable Long storeId) {
        return storeService.monthlySales(storeId);
    }

    // Method: stores.reports.inventory
    // Must be authenticated to get the inventory status report for a store
    @Operation(summary = "Get Inventory Status Report", description = "Retrieve the inventory status report for a store")
    @GetMapping("/{storeId}/reports/inventory")
    public List<Product> inventoryStatus(@PathVariable Long storeId) {
        return storeService.inventoryStatus(storeId);
    }
}
