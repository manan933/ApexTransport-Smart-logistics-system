package com.apextransport.service;

import com.apextransport.entity.Notification;
import com.apextransport.entity.User;
import com.apextransport.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public Notification notifyUser(User user, String title, String message, String type, String link) {
        Notification notification = new Notification(user, title, message, type, link);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification notifyTransporter(User transporter, String title, String message, String link) {
        return notifyUser(transporter, title, message, "ORDER", link);
    }

    @Transactional
    public Notification notifyDriver(User driver, String title, String message, String link) {
        return notifyUser(driver, title, message, "SUCCESS", link);
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findTop100ByUserOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> list = notificationRepository.findTop100ByUserOrderByCreatedAtDesc(user);
        for (Notification n : list) {
            n.setRead(true);
        }
        notificationRepository.saveAll(list);
    }
}
