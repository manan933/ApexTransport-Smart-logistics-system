package com.apextransport.controller;

import com.apextransport.entity.Order;
import com.apextransport.entity.User;
import com.apextransport.repository.OrderRepository;
import com.apextransport.repository.UserRepository;
import com.apextransport.service.AuditLogService;
import com.apextransport.service.DriverService;
import com.apextransport.service.NotificationService;
import com.apextransport.service.StorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RestController
@RequestMapping("/api/driver")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" }, allowCredentials = "true")
public class RestDriverController {

    private static final Logger log = LoggerFactory.getLogger(RestDriverController.class);

    @Autowired
    private DriverService driverService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditLogService auditLogService;

    private User getLoggedInUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return null;
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getRole() != User.Role.DRIVER)
            return null;
        return user;
    }

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getDashboard(HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        try {
            Optional<Order> currentJob = driverService.getCurrentJob(driver);
            List<Order> availableOrders = driverService.getAvailableOrders(driver);
            List<Order> completedOrders = driverService.getCompletedOrders(driver);

            double totalEarnings = completedOrders.stream()
                    .mapToDouble(o -> o.getAmount() != null ? o.getAmount() : 0.0)
                    .sum();

            Map<String, Object> resp = new HashMap<>();
            resp.put("driver", RestAuthController.formatUser(driver));
            resp.put("currentJob", currentJob.map(RestTransporterController::formatOrder).orElse(null));
            resp.put("availableCount", availableOrders.size());
            resp.put("completedCount", completedOrders.size());
            resp.put("totalEarnings", totalEarnings);
            resp.put("availableOrders", availableOrders.stream().map(RestTransporterController::formatOrder).toList());

            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("Failed to load driver dashboard", e);
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Failed to load dashboard: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @GetMapping("/orders/available")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAvailableOrders(HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        try {
            List<Order> list = driverService.getAvailableOrders(driver);
            return ResponseEntity.ok(list.stream().map(RestTransporterController::formatOrder).toList());
        } catch (Exception e) {
            log.error("Failed to load available orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/orders/current")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getCurrentJob(HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        try {
            Optional<Order> current = driverService.getCurrentJob(driver);
            Map<String, Object> resp = new HashMap<>();
            resp.put("hasJob", current.isPresent());
            resp.put("job", current.map(RestTransporterController::formatOrder).orElse(null));
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("Failed to load current job", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/orders/completed")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getCompletedOrders(HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        try {
            List<Order> list = driverService.getCompletedOrders(driver);
            return ResponseEntity.ok(list.stream().map(RestTransporterController::formatOrder).toList());
        } catch (Exception e) {
            log.error("Failed to load completed orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/orders/{id}/accept")
    public ResponseEntity<?> acceptOrder(@PathVariable Long id, HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        boolean accepted = driverService.acceptOrder(id, driver);
        if (!accepted) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unable to accept order. You may already have an active job or the order was claimed.");
            return ResponseEntity.badRequest().body(err);
        }

        Optional<Order> order = orderRepository.findById(id);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "Order accepted! Navigate to Current Job to start transit.");
        resp.put("order", order.map(RestTransporterController::formatOrder).orElse(null));

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/orders/{id}/start")
    public ResponseEntity<?> startTransit(@PathVariable Long id, HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        boolean started = driverService.startTransit(id, driver);
        if (!started) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unable to start transit for this order.");
            return ResponseEntity.badRequest().body(err);
        }

        Optional<Order> order = orderRepository.findById(id);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "Shipment is now In Transit. Real-time GPS enabled.");
        resp.put("order", order.map(RestTransporterController::formatOrder).orElse(null));

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/orders/{id}/complete")
    public ResponseEntity<?> completeOrder(
            @PathVariable Long id,
            @RequestParam(value = "podFile", required = false) MultipartFile podFile,
            @RequestParam(value = "cameraBase64", required = false) String cameraBase64,
            HttpSession session) {

        User driver = getLoggedInUser(session);
        if (driver == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        try {
            boolean completed = driverService.completeOrder(id, driver, podFile, cameraBase64);
            if (!completed) {
                Map<String, Object> err = new HashMap<>();
                err.put("error", "Unable to complete order.");
                return ResponseEntity.badRequest().body(err);
            }

            Optional<Order> order = orderRepository.findById(id);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Delivery completed and Proof of Delivery (POD) uploaded successfully!");
            resp.put("order", order.map(RestTransporterController::formatOrder).orElse(null));

            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/orders/{id}/rate-shipper")
    public ResponseEntity<?> rateShipper(
            @PathVariable Long id,
            @RequestParam("rating") Double rating,
            @RequestParam(value = "review", required = false) String review,
            HttpSession session) {

        User driver = getLoggedInUser(session);
        if (driver == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        boolean success = driverService.rateShipper(id, driver, rating, review);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Shipper rating submitted successfully!"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Unable to submit shipper rating."));
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        return ResponseEntity.ok(auditLogService.getUserLogs(driver));
    }

    @PostMapping("/orders/{id}/skip")
    public ResponseEntity<?> skipOrder(@PathVariable Long id, HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        driverService.skipOrder(id, driver);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "Order skipped from your board");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/orders/{id}/location")
    public ResponseEntity<?> updateLocation(
            @PathVariable Long id,
            @RequestParam("lat") Double lat,
            @RequestParam("lng") Double lng,
            HttpSession session) {

        User driver = getLoggedInUser(session);
        if (driver == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        if (lat == null || lng == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Latitude and longitude coordinates are required."));
        }

        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Order order = orderOpt.get();
        if (order.getDriver() != null && order.getDriver().getId().equals(driver.getId())) {
            order.setCurrentLat(lat);
            order.setCurrentLng(lng);
            orderRepository.save(order);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("lat", lat);
        resp.put("lng", lng);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        return ResponseEntity.ok(RestAuthController.formatUser(driver));
    }

    @PostMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("name") String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "vehicleNumber", required = false) String vehicleNumber,
            @RequestParam(value = "vehicleModel", required = false) String vehicleModel,
            @RequestParam(value = "vehicleType", required = false) String vehicleType,
            @RequestParam(value = "additionalVehicles", required = false) String additionalVehicles,
            @RequestParam(value = "licenseNumber", required = false) String licenseNumber,
            @RequestParam(value = "upiId", required = false) String upiId,
            @RequestParam(value = "avatarUrl", required = false) String avatarUrl,
            @RequestParam(value = "bannerUrl", required = false) String bannerUrl,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "bannerFile", required = false) MultipartFile bannerFile,
            @RequestParam(value = "vehiclePhotoFile", required = false) MultipartFile vehiclePhotoFile,
            @RequestParam(value = "upiQrFile", required = false) MultipartFile upiQrFile,
            HttpSession session) {

        User driver = getLoggedInUser(session);
        if (driver == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        try {
            User updated = driverService.updateProfile(
                    driver, name, phone, vehicleNumber, vehicleModel, vehicleType, additionalVehicles, licenseNumber, upiId,
                    avatarUrl, bannerUrl, avatarFile, bannerFile, vehiclePhotoFile, upiQrFile);

            session.setAttribute("userName", updated.getName());
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Driver profile updated successfully");
            resp.put("user", RestAuthController.formatUser(updated));
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/fleet-vehicles")
    @Transactional
    public ResponseEntity<?> updateFleetVehicles(@RequestBody(required = false) Map<String, String> payload, HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        if (payload == null) {
            payload = Collections.emptyMap();
        }

        String primaryVehicle = payload.get("vehicleType");
        String additionalVehicles = payload.get("additionalVehicles");

        if (primaryVehicle != null && !primaryVehicle.isBlank()) {
            driver.setVehicleType(primaryVehicle.trim());
        }
        if (additionalVehicles != null) {
            driver.setAdditionalVehicles(additionalVehicles.trim());
        }
        userRepository.save(driver);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Fleet vehicle configuration updated!",
                "user", RestAuthController.formatUser(driver)));
    }

    @PostMapping("/emergency")
    public ResponseEntity<?> reportEmergency(@RequestBody(required = false) Map<String, String> payload, HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        if (payload == null) {
            payload = Collections.emptyMap();
        }

        String emergencyType = payload.getOrDefault("type", "GENERAL_BREAKDOWN");
        String notes = payload.getOrDefault("notes", "Driver reported roadside assistance required");

        driverService.reportEmergency(driver, emergencyType, notes);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Highway Emergency Beacon activated! Dispatchers alerted.",
                "status", emergencyType));
    }

    @PostMapping("/emergency/resolve")
    public ResponseEntity<?> resolveEmergency(HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        driverService.resolveEmergency(driver);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Highway Emergency Beacon deactivated. All systems nominal."));
    }

    @GetMapping("/backhauls")
    public ResponseEntity<?> getBackhauls(@RequestParam(value = "city", required = false) String city, HttpSession session) {
        User driver = getLoggedInUser(session);
        if (driver == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        List<Order> available = driverService.getAvailableOrders(driver);
        if (city != null && !city.isBlank()) {
            String q = city.trim().toLowerCase();
            available = available.stream()
                    .filter(o -> o.getPickupLocation() != null && o.getPickupLocation().toLowerCase().contains(q))
                    .toList();
        }

        return ResponseEntity.ok(available.stream().map(RestTransporterController::formatOrder).toList());
    }
}
