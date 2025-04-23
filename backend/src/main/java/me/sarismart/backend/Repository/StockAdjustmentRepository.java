package me.sarismart.backend.Repository;

import me.sarismart.backend.Entity.StockAdjustment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {
    List<StockAdjustment> findByStoreId(Long storeId);
    List<StockAdjustment> findByStoreIdAndProductId(Long storeId, Long productId);
}