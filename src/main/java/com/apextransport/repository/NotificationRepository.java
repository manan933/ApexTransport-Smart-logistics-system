package com.apextransport.repository;

import com.apextransport.entity.Notification;
import com.apextransport.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop100ByUserOrderByCreatedAtDesc(User user);
    List<Notification> findTop15ByUserOrderByCreatedAtDesc(User user);
    long countByUserAndIsReadFalse(User user);
}
