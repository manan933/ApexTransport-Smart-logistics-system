package com.apextransport.service;

import com.apextransport.entity.Notification;
import com.apextransport.entity.Order;
import com.apextransport.entity.SkippedOrder;
import com.apextransport.entity.User;
import com.apextransport.repository.NotificationRepository;
import com.apextransport.repository.OrderRepository;
import com.apextransport.repository.SkippedOrderRepository;
import com.apextransport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkippedOrderRepository skippedOrderRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private AuditLogService auditLogService;

    public List<Order> getAvailableOrders(User driver) {
        List<Order> allPending = orderRepository.findByStatusOrderByCreatedAtDesc(Order.OrderStatus.PENDING);
        if (driver == null) {
            return allPending;
        }
        return allPending.stream()
                .filter(order -> !skippedOrderRepository.existsByDriverAndOrder(driver, order))
                .toList();
    }

    public boolean isVehicleCompatible(User driver, String orderVehicleType) {
        return true;
    }

    public List<Order> getAllPendingOrders() {
        return orderRepository.findByStatusOrderByCreatedAtDesc(Order.OrderStatus.PENDING);
    }

    public Optional<Order> getCurrentJob(User driver) {
        return orderRepository.findFirstByDriverAndStatusInOrderByCreatedAtDesc(driver,
                Arrays.asList(Order.OrderStatus.ACCEPTED, Order.OrderStatus.IN_TRANSIT));
    }

    public List<Order> getCompletedOrders(User driver) {
        return orderRepository.findByDriverAndStatusOrderByCompletedAtDesc(driver, Order.OrderStatus.COMPLETED);
    }

    @Transactional
    public boolean acceptOrder(Long orderId, User driver) {
        if (getCurrentJob(driver).isPresent()) {
            return false;
        }

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty())
            return false;

        Order order = orderOpt.get();
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            return false;
        }

        order.setDriver(driver);
        order.setStatus(Order.OrderStatus.ACCEPTED);
        order.setAcceptedAt(LocalDateTime.now());
        try {
            orderRepository.saveAndFlush(order);
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            return false;
        }

        // Notify Transporter
        Notification transNotif = new Notification(
                order.getTransporter(),
                "Driver Assigned! 🚚",
                String.format("Driver %s (%s, ⭐%.1f) has accepted Order #%d! Preparing dispatch.",
                        driver.getName(),
                        driver.getVehicleNumber() != null ? driver.getVehicleNumber() : "Fleet Truck",
                        driver.getRating() != null ? driver.getRating() : 5.0,
                        order.getId()),
                "SUCCESS",
                "/transporter/my-orders");
        notificationRepository.save(transNotif);

        // Notification for Driver
        Notification driverNotif = new Notification(
                driver,
                "Job Confirmed! 📦",
                "You have accepted Order #" + order.getId() + ". Navigate to Current Job to start transit.",
                "SUCCESS",
                "/driver/current-job");
        notificationRepository.save(driverNotif);

        auditLogService.log(
                driver,
                "CLAIMED_ORDER",
                "ORDER",
                String.format("Driver claimed Consignment #%d (%s -> %s)", order.getId(), order.getPickupLocation(),
                        order.getDropLocation()));

        return true;
    }

    @Transactional
    public boolean startTransit(Long orderId, User driver) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty())
            return false;

        Order order = orderOpt.get();
        if (order.getDriver() == null || !order.getDriver().getId().equals(driver.getId())) {
            return false;
        }
        if (order.getStatus() != Order.OrderStatus.ACCEPTED) {
            return false;
        }

        order.setStatus(Order.OrderStatus.IN_TRANSIT);
        order.setStartedAt(LocalDateTime.now());

        if (order.getPickupLat() != null && order.getDropLat() != null
                && order.getPickupLng() != null && order.getDropLng() != null) {
            order.setCurrentLat((order.getPickupLat() + order.getDropLat()) / 2.0);
            order.setCurrentLng((order.getPickupLng() + order.getDropLng()) / 2.0);
        }

        orderRepository.save(order);

        Notification notif = new Notification(
                order.getTransporter(),
                "Shipment In Transit 🚛",
                "Order #" + order.getId() + " is now in transit with Driver " + driver.getName() + ".",
                "INFO",
                "/transporter/my-orders");
        notificationRepository.save(notif);

        auditLogService.log(
                driver,
                "STARTED_TRANSIT",
                "ORDER",
                "Driver commenced transit for Order #" + order.getId());

        return true;
    }

    @Transactional
    public boolean completeOrder(Long orderId, User driver, MultipartFile podFile, String cameraBase64)
            throws IOException {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty())
            return false;

        Order order = orderOpt.get();
        if (order.getDriver() == null || !order.getDriver().getId().equals(driver.getId())) {
            return false;
        }
        if (order.getStatus() != Order.OrderStatus.IN_TRANSIT && order.getStatus() != Order.OrderStatus.ACCEPTED) {
            return false;
        }

        // Mandatory Proof of Delivery Check
        String podUrl = null;
        if (cameraBase64 != null && !cameraBase64.trim().isEmpty()) {
            podUrl = storageService.uploadBase64Image(cameraBase64, "pod");
        } else if (podFile != null && !podFile.isEmpty()) {
            podUrl = storageService.uploadFile(podFile, "pod");
        }

        if (podUrl == null || podUrl.isBlank()) {
            throw new IllegalArgumentException("Proof of Delivery (POD) photo is mandatory to complete delivery.");
        }

        order.setPodImageUrl(podUrl);
        order.setStatus(Order.OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        if (order.getDropLat() != null) {
            order.setCurrentLat(order.getDropLat());
            order.setCurrentLng(order.getDropLng());
        }
        orderRepository.save(order);

        // Update driver delivery stats
        driver.setTotalDeliveries((driver.getTotalDeliveries() != null ? driver.getTotalDeliveries() : 0) + 1);
        userRepository.save(driver);

        // Notify Transporter
        Notification notif = new Notification(
                order.getTransporter(),
                "Shipment Delivered! 🎉",
                String.format("Order #%d has been delivered by Driver %s. Proof of Delivery is ready for verification.",
                        order.getId(), driver.getName()),
                "SUCCESS",
                "/transporter/my-orders");
        notificationRepository.save(notif);

        auditLogService.log(
                driver,
                "COMPLETED_DELIVERY",
                "ORDER",
                String.format("Delivery completed for Order #%d. POD uploaded successfully.", order.getId()));

        return true;
    }

    @Transactional
    public boolean rateShipper(Long orderId, User driver, Double rating, String review) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty())
            return false;

        Order order = orderOpt.get();
        if (order.getDriver() == null || !order.getDriver().getId().equals(driver.getId())) {
            return false;
        }

        if (rating != null && rating >= 1.0 && rating <= 5.0) {
            order.setShipperRating(rating);
            order.setShipperReview(review);
            orderRepository.save(order);

            User transporter = order.getTransporter();
            if (transporter != null) {
                int priorCount = transporter.getShipperRatingCount() != null ? transporter.getShipperRatingCount() : 0;
                double currentRating = transporter.getShipperRating() != null ? transporter.getShipperRating() : 0.0;
                double newRating = priorCount == 0 ? rating
                        : ((currentRating * priorCount) + rating) / (priorCount + 1);
                transporter.setShipperRating(Math.round(newRating * 10.0) / 10.0);
                transporter.setShipperRatingCount(priorCount + 1);
                userRepository.save(transporter);

                // Notify Shipper
                Notification notif = new Notification(
                        transporter,
                        "⭐ New Shipper Rating Received!",
                        String.format("Driver %s rated your loading experience %.1f/5.0: \"%s\"",
                                driver.getName(), rating, review != null ? review : "Smooth loading & fast clearance"),
                        "SUCCESS",
                        "/transporter/profile");
                notificationRepository.save(notif);
            }

            auditLogService.log(
                    driver,
                    "RATED_SHIPPER",
                    "RATING",
                    String.format("Driver rated Shipper %.1f stars for Order #%d", rating, order.getId()));

            return true;
        }
        return false;
    }

    @Transactional
    public void skipOrder(Long orderId, User driver) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            if (!skippedOrderRepository.existsByDriverAndOrder(driver, orderOpt.get())) {
                skippedOrderRepository.save(new SkippedOrder(driver, orderOpt.get()));
            }
        }
    }

    @Transactional
    public User updateProfile(User driver,
            String name,
            String phone,
            String vehicleNumber,
            String vehicleModel,
            String vehicleType,
            String additionalVehicles,
            String licenseNumber,
            String upiId,
            String avatarUrl,
            String bannerUrl,
            MultipartFile avatar,
            MultipartFile banner,
            MultipartFile vehiclePhoto,
            MultipartFile upiQr) throws IOException {

        if (name != null && !name.trim().isEmpty())
            driver.setName(name);
        if (phone != null)
            driver.setPhone(phone);
        if (vehicleNumber != null)
            driver.setVehicleNumber(vehicleNumber);
        if (vehicleModel != null)
            driver.setVehicleModel(vehicleModel);
        if (vehicleType != null)
            driver.setVehicleType(vehicleType);
        if (additionalVehicles != null)
            driver.setAdditionalVehicles(additionalVehicles);
        if (licenseNumber != null)
            driver.setLicenseNumber(licenseNumber);
        if (upiId != null)
            driver.setUpiId(upiId);

        if (avatar != null && !avatar.isEmpty()) {
            driver.setAvatarUrl(storageService.uploadFile(avatar, "avatars"));
        } else if (avatarUrl != null && !avatarUrl.isBlank()) {
            if (avatarUrl.startsWith("data:image")) {
                driver.setAvatarUrl(storageService.uploadBase64Image(avatarUrl, "avatars"));
            } else {
                driver.setAvatarUrl(avatarUrl);
            }
        }

        if (banner != null && !banner.isEmpty()) {
            driver.setBannerUrl(storageService.uploadFile(banner, "banners"));
        } else if (bannerUrl != null && !bannerUrl.isBlank()) {
            if (bannerUrl.startsWith("data:image")) {
                driver.setBannerUrl(storageService.uploadBase64Image(bannerUrl, "banners"));
            } else {
                driver.setBannerUrl(bannerUrl);
            }
        }

        if (vehiclePhoto != null && !vehiclePhoto.isEmpty()) {
            driver.setVehiclePhotoUrl(storageService.uploadFile(vehiclePhoto, "vehicles"));
        }
        if (upiQr != null && !upiQr.isEmpty()) {
            driver.setUpiQrUrl(storageService.uploadFile(upiQr, "qr"));
        }

        User updated = userRepository.save(driver);

        auditLogService.log(
                driver,
                "UPDATED_PROFILE",
                "PROFILE",
                "Driver profile updated");

        return updated;
    }

    @Transactional
    public void reportEmergency(User driver, String emergencyType, String notes) {
        driver.setEmergencyStatus(emergencyType);
        userRepository.save(driver);

        Optional<Order> currentJob = getCurrentJob(driver);
        String jobInfo = currentJob.map(o -> " on Active Order #" + o.getId() + " (" + o.getPickupLocation() + " -> " + o.getDropLocation() + ")").orElse("");

        // Notify active transporter if on mission
        if (currentJob.isPresent() && currentJob.get().getTransporter() != null) {
            Notification transNotif = new Notification(
                    currentJob.get().getTransporter(),
                    "🚨 HIGHWAY EMERGENCY ALERT: " + emergencyType,
                    String.format("Driver %s (%s) has triggered an emergency beacon: %s. Notes: %s%s",
                            driver.getName(), driver.getPhone() != null ? driver.getPhone() : "N/A", emergencyType, notes, jobInfo),
                    "ALERT",
                    "/transporter/my-orders");
            notificationRepository.save(transNotif);
        }

        auditLogService.log(
                driver,
                "EMERGENCY_BEACON",
                "SECURITY",
                String.format("Emergency beacon triggered: %s - %s%s", emergencyType, notes, jobInfo));
    }

    @Transactional
    public void resolveEmergency(User driver) {
        driver.setEmergencyStatus(null);
        userRepository.save(driver);

        auditLogService.log(
                driver,
                "EMERGENCY_RESOLVED",
                "SECURITY",
                "Emergency status cleared by driver");
    }
}
