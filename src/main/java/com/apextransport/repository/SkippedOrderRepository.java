package com.apextransport.repository;

import com.apextransport.entity.SkippedOrder;
import com.apextransport.entity.Order;
import com.apextransport.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkippedOrderRepository extends JpaRepository<SkippedOrder, Long> {
    boolean existsByDriverAndOrder(User driver, Order order);
    Optional<SkippedOrder> findByDriverAndOrder(User driver, Order order);
}
