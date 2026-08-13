package com.apextransport.controller;

import com.apextransport.entity.Notification;
import com.apextransport.entity.Order;
import com.apextransport.entity.User;
import com.apextransport.repository.NotificationRepository;
import com.apextransport.repository.OrderRepository;
import com.apextransport.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private static final List<String> POPULAR_LOCATIONS = Arrays.asList(
            "Mumbai Port Terminal, Mumbai, Maharashtra",
            "Bhiwandi Logistics Hub, Thane, Maharashtra",
            "JNPT Port, Navi Mumbai, Maharashtra",
            "Pune Auto Cluster, Chakan, Maharashtra",
            "Nehru Place Logistics Yard, New Delhi, Delhi",
            "Okhla Industrial Area, New Delhi, Delhi",
            "Gurugram Cyber Hub Freight Zone, Haryana",
            "Whitefield EPIP Logistics Zone, Bengaluru, Karnataka",
            "Peenya Industrial Area, Bengaluru, Karnataka",
            "Genome Valley Cold Chain, Hyderabad, Telangana",
            "Patancheru Industrial Park, Hyderabad, Telangana",
            "Chennai Harbor Freight Terminal, Chennai, Tamil Nadu",
            "Sriperumbudur Industrial Hub, Chennai, Tamil Nadu",
            "Surat Textile Market Hub, Surat, Gujarat",
            "Sanand Industrial Estate, Ahmedabad, Gujarat",
            "Kolkata Port Trust Yard, Kolkata, West Bengal",
            "Bhubaneswar AIIMS Logistics Depot, Bhubaneswar, Odisha",
            "Cuttack Malgodown Wholesale Hub, Cuttack, Odisha",
            "Paradeep Port Freight Gate, Jagatsinghpur, Odisha",
            "Nagpur Butibori Multi-Modal Cargo Hub, Maharashtra",
            "Indore Pithampur Industrial Corridor, Madhya Pradesh",
            "Jaipur Sitapura Industrial Area, Rajasthan",
            "Chandigarh Freight Terminal, Punjab / Haryana",
            "Lucknow Transport Nagar, Uttar Pradesh",
            "Kanpur Panki Freight Terminal, Uttar Pradesh");

    private User getLoggedInUser(HttpSession session) {
        if (session == null)
            return null;
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return null;
        return userRepository.findById(userId).orElse(null);
    }

    @GetMapping("/notifications/recent")
    public ResponseEntity<?> getRecentNotifications(HttpSession session) {
        User user = getLoggedInUser(session);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        List<Notification> list = notificationRepository.findTop15ByUserOrderByCreatedAtDesc(user);
        long unreadCount = notificationRepository.countByUserAndIsReadFalse(user);

        return ResponseEntity.ok(Map.of(
                "notifications", list,
                "unreadCount", unreadCount));
    }

    @PostMapping("/notifications/mark-read/{id}")
    @Transactional
    public ResponseEntity<?> markNotificationRead(@PathVariable Long id, HttpSession session) {
        User user = getLoggedInUser(session);
        if (user == null)
            return ResponseEntity.status(401).build();

        Optional<Notification> nOpt = notificationRepository.findById(id);
        if (nOpt.isEmpty() || !nOpt.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Notification not found"));
        }

        Notification n = nOpt.get();
        n.setRead(true);
        notificationRepository.save(n);

        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/locations/suggest")
    public ResponseEntity<?> suggestLocations(@RequestParam(required = false, defaultValue = "") String q) {
        String query = q.toLowerCase().trim();
        List<String> matches = new ArrayList<>();

        if (query.isEmpty()) {
            matches.addAll(POPULAR_LOCATIONS.subList(0, Math.min(8, POPULAR_LOCATIONS.size())));
        } else {
            for (String loc : POPULAR_LOCATIONS) {
                if (loc.toLowerCase().contains(query)) {
                    matches.add(loc);
                }
            }
        }

        return ResponseEntity.ok(matches);
    }

    @GetMapping("/orders/{id}/coordinates")
    public ResponseEntity<?> getOrderCoordinates(@PathVariable Long id, HttpSession session) {
        User user = getLoggedInUser(session);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        boolean isTransporter = order.getTransporter() != null && order.getTransporter().getId().equals(user.getId());
        boolean isDriver = order.getDriver() != null && order.getDriver().getId().equals(user.getId());
        if (!isTransporter && !isDriver && user.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", order.getId());
        data.put("status", order.getStatus().name());
        data.put("pickupLocation", order.getPickupLocation());
        data.put("dropLocation", order.getDropLocation());
        data.put("pickupLat", order.getPickupLat() != null ? order.getPickupLat() : 19.0760);
        data.put("pickupLng", order.getPickupLng() != null ? order.getPickupLng() : 72.8777);
        data.put("dropLat", order.getDropLat() != null ? order.getDropLat() : 28.6139);
        data.put("dropLng", order.getDropLng() != null ? order.getDropLng() : 77.2090);
        data.put("currentLat", order.getCurrentLat() != null ? order.getCurrentLat() : order.getPickupLat());
        data.put("currentLng", order.getCurrentLng() != null ? order.getCurrentLng() : order.getPickupLng());
        data.put("driverName", order.getDriver() != null ? order.getDriver().getName() : "Not assigned");
        data.put("vehicleNumber", order.getDriver() != null ? order.getDriver().getVehicleNumber() : "—");
        data.put("goodsType", order.getGoodsType());

        return ResponseEntity.ok(data);
    }
}
