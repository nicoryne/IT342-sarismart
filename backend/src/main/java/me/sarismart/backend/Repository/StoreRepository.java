package me.sarismart.backend.Repository;

import me.sarismart.backend.Entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByOwner_SupabaseUid(String supabaseUid);
    List<Store> findByWorkers_SupabaseUid(String supabaseUid);

    @Query(value = """
        SELECT * FROM stores
        WHERE (
            6371 * acos(
                cos(radians(:latitude)) * cos(radians(latitude)) *
                cos(radians(longitude) - radians(:longitude)) +
                sin(radians(:latitude)) * sin(radians(latitude))
            )
        ) <= :radius
        """, nativeQuery = true)
    List<Store> findNearbyStores(
        @Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("radius") double radius
    );
}