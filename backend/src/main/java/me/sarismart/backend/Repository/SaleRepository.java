package me.sarismart.backend.Repository;

import me.sarismart.backend.Entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByStoreIdAndSaleDateBetween(Long storeId, LocalDateTime start, LocalDateTime end);
}
