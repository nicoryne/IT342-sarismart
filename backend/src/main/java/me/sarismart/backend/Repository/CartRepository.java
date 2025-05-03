package me.sarismart.backend.Repository;

import me.sarismart.backend.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.store.id = :storeId")
    List<Cart> findByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT c FROM Cart c WHERE c.seller.supabaseUid = :ownerId")
    List<Cart> findByOwnerId(@Param("ownerId") String ownerId);

    @Query("SELECT c FROM Cart c WHERE LOWER(c.cartName) LIKE LOWER(CONCAT('%', :cartName, '%'))")
    List<Cart> findByCartNameContainingIgnoreCase(@Param("cartName") String cartName);
}
