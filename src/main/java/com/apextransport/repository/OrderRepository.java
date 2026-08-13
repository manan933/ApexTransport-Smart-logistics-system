package com.apextransport.repository;

import com.apextransport.entity.Order;
import com.apextransport.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status);
    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findByTransporterOrderByCreatedAtDesc(User transporter);
    List<Order> findByDriverAndStatusOrderByCompletedAtDesc(User driver, Order.OrderStatus status);
    Optional<Order> findFirstByDriverAndStatusInOrderByCreatedAtDesc(User driver, List<Order.OrderStatus> statuses);
    List<Order> findByDriverOrderByCreatedAtDesc(User driver);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND NOT EXISTS " +
           "(SELECT 1 FROM SkippedOrder s WHERE s.order = o AND s.driver = :driver) " +
           "ORDER BY o.createdAt DESC")
    List<Order> findPendingOrdersNotSkippedByDriver(@Param("driver") User driver, @Param("status") Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findAllPendingOrders(@Param("status") Order.OrderStatus status);

    long countByStatus(Order.OrderStatus status);
    long countByDriver(User driver);
    long countByTransporter(User transporter);
}
