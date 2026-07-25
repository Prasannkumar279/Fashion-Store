package com.fashionstore.repository;

import com.fashionstore.entity.ShopOrder;
import com.fashionstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {
    List<ShopOrder> findByUserOrderByCreatedAtDesc(User user);
}
