package com.apextransport.controller;

import com.apextransport.entity.Order;
import com.apextransport.entity.User;
import com.apextransport.repository.OrderRepository;
import com.apextransport.repository.UserRepository;
import com.apextransport.service.AuditLogService;
import com.apextransport.service.NotificationService;
import com.apextransport.service.StorageService;
import com.apextransport.service.TransporterService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/transporter")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" }, allowCredentials = "true")
public class RestTransporterController {

    @Autowired
    private TransporterService transporterService;

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
        if (user == null || user.getRole() != User.Role.TRANSPORTER)
            return null;
        return user;
    }

    public static Map<String, Object> formatOrder(Order o) {
        if (o == null) return Collections.emptyMap();
        Map<String, Object> m = new HashMap<>();
        m.put("id", o.getId());
        m.put("pickupLocation", o.getPickupLocation() != null ? o.getPickupLocation() : "");
        m.put("dropLocation", o.getDropLocation() != null ? o.getDropLocation() : "");
        m.put("pickupLat", o.getPickupLat() != null ? o.getPickupLat() : 19.0760);
        m.put("pickupLng", o.getPickupLng() != null ? o.getPickupLng() : 72.8777);
        m.put("dropLat", o.getDropLat() != null ? o.getDropLat() : 28.6139);
        m.put("dropLng", o.getDropLng() != null ? o.getDropLng() : 77.2090);
        m.put("currentLat", o.getCurrentLat() != null ? o.getCurrentLat() : (o.getPickupLat() != null ? o.getPickupLat() : 19.0760));
        m.put("currentLng", o.getCurrentLng() != null ? o.getCurrentLng() : (o.getPickupLng() != null ? o.getPickupLng() : 72.8777));
        m.put("distanceKm", o.getDistanceKm() != null ? o.getDistanceKm() : 0.0);
        m.put("goodsType", o.getGoodsType() != null ? o.getGoodsType() : "General Cargo");
        m.put("goodsDescription", o.getGoodsDescription() != null ? o.getGoodsDescription() : "");
        m.put("contactPersonName", o.getContactPersonName() != null ? o.getContactPersonName() : "");
        m.put("contactPersonPhone", o.getContactPersonPhone() != null ? o.getContactPersonPhone() : "");
        m.put("useShipperDetails", Boolean.TRUE.equals(o.getUseShipperDetails()));
        m.put("pickupDate", o.getPickupDate() != null ? o.getPickupDate() : "");
        m.put("pickupTimeSlot", o.getPickupTimeSlot() != null ? o.getPickupTimeSlot() : "");
        m.put("dimensionLength", o.getDimensionLength());
        m.put("dimensionWidth", o.getDimensionWidth());
        m.put("dimensionHeight", o.getDimensionHeight());
        m.put("dimensionUnit", o.getDimensionUnit() != null ? o.getDimensionUnit() : "cm");
        m.put("packageCount", o.getPackageCount() != null ? o.getPackageCount() : 1);
        m.put("isFragile", Boolean.TRUE.equals(o.getIsFragile()));
        m.put("isHazardous", Boolean.TRUE.equals(o.getIsHazardous()));
        m.put("isTempControlled", Boolean.TRUE.equals(o.getIsTempControlled()));
        m.put("targetTemp", o.getTargetTemp());
        m.put("isStackable", !Boolean.FALSE.equals(o.getIsStackable()));
        m.put("invoiceUrl", o.getInvoiceUrl());
        m.put("docUrls", o.getDocUrls() != null ? Arrays.asList(o.getDocUrls().split(",")) : Collections.emptyList());
        m.put("driverNotes", o.getDriverNotes() != null ? o.getDriverNotes() : "");

        m.put("weight", o.getWeight() != null ? o.getWeight() : 0.0);
        m.put("vehicleType", o.getVehicleType() != null ? o.getVehicleType() : "Standard Truck");
        m.put("fare", o.getAmount() != null ? o.getAmount() : 0.0);
        m.put("amount", o.getAmount() != null ? o.getAmount() : 0.0);
        m.put("price", o.getAmount() != null ? o.getAmount() : 0.0);
        m.put("preferredTime", o.getPreferredTime() != null ? o.getPreferredTime() : "Flexible");
        m.put("customPickupTime", o.getCustomPickupTime() != null ? o.getCustomPickupTime().toString() : "");
        m.put("status", o.getStatus() != null ? o.getStatus().name() : "PENDING");
        m.put("paymentMethod", o.getPaymentMethod() != null ? o.getPaymentMethod().name() : "COD");
        m.put("paymentStatus", o.getPaymentStatus() != null ? o.getPaymentStatus().name() : "PENDING");
        m.put("imagePath", o.getImagePath());
        m.put("podImageUrl", o.getPodImageUrl());
        m.put("driverRating", o.getDriverRating());
        m.put("driverReview", o.getDriverReview());
        m.put("shipperRating", o.getShipperRating());
        m.put("shipperReview", o.getShipperReview());
        m.put("createdAt", o.getCreatedAt() != null ? o.getCreatedAt().toString() : "");
        m.put("acceptedAt", o.getAcceptedAt() != null ? o.getAcceptedAt().toString() : "");
        m.put("startedAt", o.getStartedAt() != null ? o.getStartedAt().toString() : "");
        m.put("completedAt", o.getCompletedAt() != null ? o.getCompletedAt().toString() : "");

        try {
            if (o.getTransporter() != null) {
                Map<String, Object> t = new HashMap<>();
                t.put("id", o.getTransporter().getId());
                t.put("name", o.getTransporter().getName() != null ? o.getTransporter().getName() : "Shipper");
                t.put("companyName", o.getTransporter().getCompanyName() != null ? o.getTransporter().getCompanyName() : "");
                t.put("phone", o.getTransporter().getPhone() != null ? o.getTransporter().getPhone() : "");
                t.put("shipperRating",
                        o.getTransporter().getShipperRating() != null ? o.getTransporter().getShipperRating() : 5.0);
                t.put("totalShippedOrders",
                        o.getTransporter().getTotalShippedOrders() != null ? o.getTransporter().getTotalShippedOrders()
                                : 0);
                m.put("transporter", t);
            }
        } catch (Exception ignored) {
        }

        try {
            if (o.getDriver() != null) {
                Map<String, Object> d = new HashMap<>();
                d.put("id", o.getDriver().getId());
                d.put("name", o.getDriver().getName() != null ? o.getDriver().getName() : "Driver");
                d.put("phone", o.getDriver().getPhone() != null ? o.getDriver().getPhone() : "");
                d.put("vehicleNumber", o.getDriver().getVehicleNumber() != null ? o.getDriver().getVehicleNumber() : "");
                d.put("vehicleType", o.getDriver().getVehicleType() != null ? o.getDriver().getVehicleType() : "");
                d.put("avatarUrl", o.getDriver().getAvatarUrl() != null ? o.getDriver().getAvatarUrl() : "");
                d.put("rating", o.getDriver().getRating() != null ? o.getDriver().getRating() : 5.0);
                d.put("totalDeliveries",
                        o.getDriver().getTotalDeliveries() != null ? o.getDriver().getTotalDeliveries() : 0);
                m.put("driver", d);
            }
        } catch (Exception ignored) {
        }

        return m;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpSession session) {
        User transporter = getLoggedInUser(session);
        if (transporter == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        List<Order> orders = orderRepository.findByTransporterOrderByCreatedAtDesc(transporter);
        long pendingCount = orders.stream().filter(o -> o.getStatus() == Order.OrderStatus.PENDING).count();
        long activeCount = orders.stream().filter(
                o -> o.getStatus() == Order.OrderStatus.ACCEPTED || o.getStatus() == Order.OrderStatus.IN_TRANSIT)
                .count();
        long completedCount = orders.stream().filter(o -> o.getStatus() == Order.OrderStatus.COMPLETED).count();

        List<Map<String, Object>> formattedOrders = orders.stream().map(RestTransporterController::formatOrder)
                .toList();

        return ResponseEntity.ok(Map.of(
                "totalOrders", orders.size(),
                "pendingCount", pendingCount,
                "activeCount", activeCount,
                "completedCount", completedCount,
                "orders", formattedOrders,
                "user", RestAuthController.formatUser(transporter)));
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(HttpSession session) {
        User transporter = getLoggedInUser(session);
        if (transporter == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        List<Order> orders = orderRepository.findByTransporterOrderByCreatedAtDesc(transporter);
        return ResponseEntity.ok(orders.stream().map(RestTransporterController::formatOrder).toList());
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(
            @RequestParam("goodsType") String goodsType,
            @RequestParam(value = "customGoodsType", required = false) String customGoodsType,
            @RequestParam(value = "goodsDescription", required = false) String goodsDescription,
            @RequestParam(value = "contactPersonName", required = false) String contactPersonName,
            @RequestParam(value = "contactPersonPhone", required = false) String contactPersonPhone,
            @RequestParam(value = "useShipperDetails", required = false) Boolean useShipperDetails,
            @RequestParam(value = "pickupDate", required = false) String pickupDate,
            @RequestParam(value = "pickupTimeSlot", required = false) String pickupTimeSlot,
            @RequestParam(value = "dimensionLength", required = false) Double dimensionLength,
            @RequestParam(value = "dimensionWidth", required = false) Double dimensionWidth,
            @RequestParam(value = "dimensionHeight", required = false) Double dimensionHeight,
            @RequestParam(value = "dimensionUnit", required = false) String dimensionUnit,
            @RequestParam(value = "packageCount", required = false) Integer packageCount,
            @RequestParam(value = "isFragile", required = false) Boolean isFragile,
            @RequestParam(value = "isHazardous", required = false) Boolean isHazardous,
            @RequestParam(value = "isTempControlled", required = false) Boolean isTempControlled,
            @RequestParam(value = "targetTemp", required = false) Double targetTemp,
            @RequestParam(value = "isStackable", required = false) Boolean isStackable,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "vehicleType", required = false) String vehicleType,
            @RequestParam("pickupLocation") String pickupLocation,
            @RequestParam("dropLocation") String dropLocation,
            @RequestParam(value = "pickupLat", required = false) Double pickupLat,
            @RequestParam(value = "pickupLng", required = false) Double pickupLng,
            @RequestParam(value = "dropLat", required = false) Double dropLat,
            @RequestParam(value = "dropLng", required = false) Double dropLng,
            @RequestParam(value = "preferredTime", required = false) String preferredTime,
            @RequestParam(value = "customPickupTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime customPickupTime,
            @RequestParam(value = "paymentMethod", required = false) String paymentMethodStr,
            @RequestParam(value = "amount", required = false) Double amount,
            @RequestParam(value = "driverNotes", required = false) String driverNotes,
            @RequestParam(value = "goodsImage", required = false) MultipartFile goodsImage,
            @RequestParam(value = "invoiceFile", required = false) MultipartFile invoiceFile,
            @RequestParam(value = "docFiles", required = false) List<MultipartFile> docFiles,
            @RequestParam(value = "cameraBase64", required = false) String cameraBase64,
            HttpSession session) {

        User transporter = getLoggedInUser(session);
        if (transporter == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        try {
            Order.PaymentMethod paymentMethod = Order.PaymentMethod.COD;
            if (paymentMethodStr != null) {
                try {
                    paymentMethod = Order.PaymentMethod.valueOf(paymentMethodStr.toUpperCase());
                } catch (Exception ignored) {
                }
            }

            String finalGoodsType = goodsType;
            if ("OTHER".equalsIgnoreCase(goodsType) && customGoodsType != null && !customGoodsType.isBlank()) {
                finalGoodsType = customGoodsType.trim();
            }

            Order createdOrder = transporterService.createOrder(
                    transporter, finalGoodsType, weight, vehicleType,
                    pickupLocation, dropLocation, pickupLat, pickupLng, dropLat, dropLng,
                    preferredTime, customPickupTime,
                    paymentMethod, amount, goodsImage, cameraBase64,
                    goodsDescription, contactPersonName, contactPersonPhone, useShipperDetails,
                    pickupDate, pickupTimeSlot,
                    dimensionLength, dimensionWidth, dimensionHeight, dimensionUnit,
                    packageCount, isFragile, isHazardous, isTempControlled, targetTemp, isStackable,
                    invoiceFile, docFiles, driverNotes);

            notificationService.notifyTransporter(
                    transporter,
                    "📦 Shipment Created (#" + createdOrder.getId() + ")",
                    "Your shipment from " + pickupLocation + " to " + dropLocation + " is now open for drivers.",
                    "/transporter/my-orders");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Shipment created successfully with all specifications",
                    "order", formatOrder(createdOrder)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/orders/{id}/rate-driver")
    public ResponseEntity<?> rateDriver(
            @PathVariable Long id,
            @RequestParam("rating") Double rating,
            @RequestParam(value = "review", required = false) String review,
            @RequestParam(value = "paymentMethod", required = false, defaultValue = "COD") String paymentMethod,
            HttpSession session) {

        User transporter = getLoggedInUser(session);
        if (transporter == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        boolean success = transporterService.rateDriverAndPay(id, transporter, rating, review, paymentMethod);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Driver rated and payment marked settled."));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Unable to submit driver rating. Check order status or ownership."));
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(HttpSession session) {
        User transporter = getLoggedInUser(session);
        if (transporter == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        return ResponseEntity.ok(auditLogService.getUserLogs(transporter));
    }

    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            @RequestParam(value = "reason", required = false) String reason,
            HttpSession session) {

        User transporter = getLoggedInUser(session);
        if (transporter == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        boolean cancelled = transporterService.cancelOrder(id, transporter,
                reason != null ? reason : "Cancelled by shipper");
        if (cancelled) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Shipment cancelled successfully"));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Unable to cancel shipment. It may already be in transit or completed."));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {
        User transporter = getLoggedInUser(session);
        if (transporter == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        return ResponseEntity.ok(RestAuthController.formatUser(transporter));
    }

    @PostMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("name") String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "companyAddress", required = false) String companyAddress,
            @RequestParam(value = "gstNumber", required = false) String gstNumber,
            @RequestParam(value = "upiId", required = false) String upiId,
            @RequestParam(value = "avatarUrl", required = false) String avatarUrl,
            @RequestParam(value = "bannerUrl", required = false) String bannerUrl,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            @RequestParam(value = "bannerFile", required = false) MultipartFile bannerFile,
            @RequestParam(value = "upiQrFile", required = false) MultipartFile upiQrFile,
            HttpSession session) {

        User transporter = getLoggedInUser(session);
        if (transporter == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        try {
            User updated = transporterService.updateProfile(
                    transporter, name, phone, companyName, companyAddress, gstNumber, upiId,
                    avatarUrl, bannerUrl, avatarFile, bannerFile, upiQrFile);

            session.setAttribute("userName", updated.getName());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Profile updated successfully",
                    "user", RestAuthController.formatUser(updated)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
