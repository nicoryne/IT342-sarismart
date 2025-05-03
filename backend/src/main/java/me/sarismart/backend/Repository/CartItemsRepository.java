package me.sarismart.backend.Repository;

import me.sarismart.backend.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItem, Long> {

}
