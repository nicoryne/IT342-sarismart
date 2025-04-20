package me.sarismart.backend.Repository;

import me.sarismart.backend.Entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByOwner_SupabaseUid(String supabaseUid);
}