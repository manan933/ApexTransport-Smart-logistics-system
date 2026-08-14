package com.apextransport.service;

import com.apextransport.entity.Notification;
import com.apextransport.entity.Order;
import com.apextransport.entity.User;
import com.apextransport.repository.NotificationRepository;
import com.apextransport.repository.OrderRepository;
import com.apextransport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransporterService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private AuditLogService auditLogService;

    // Fast geocode coordinate lookup for key Indian logistics hubs
    private static final Map<String, double[]> CITY_COORDS = new HashMap<>();
    static {
        CITY_COORDS.put("mumbai", new double[] { 19.0760, 72.8777 });
        CITY_COORDS.put("delhi", new double[] { 28.6139, 77.2090 });
        CITY_COORDS.put("bengaluru", new double[] { 12.9716, 77.5946 });
        CITY_COORDS.put("bangalore", new double[] { 12.9716, 77.5946 });
        CITY_COORDS.put("hyderabad", new double[] { 17.3850, 78.4867 });
        CITY_COORDS.put("chennai", new double[] { 13.0827, 80.2707 });
        CITY_COORDS.put("kolkata", new double[] { 22.5726, 88.3639 });
        CITY_COORDS.put("pune", new double[] { 18.5204, 73.8567 });
        CITY_COORDS.put("ahmedabad", new double[] { 23.0225, 72.5714 });
        CITY_COORDS.put("jaipur", new double[] { 26.9124, 75.7873 });
        CITY_COORDS.put("surat", new double[] { 21.1702, 72.8311 });
        CITY_COORDS.put("bhubaneswar", new double[] { 20.2961, 85.8245 });
        CITY_COORDS.put("cuttack", new double[] { 20.4625, 85.8828 });
        CITY_COORDS.put("nagpur", new double[] { 21.1458, 79.0882 });
        CITY_COORDS.put("indore", new double[] { 22.7196, 75.8577 });
        CITY_COORDS.put("lucknow", new double[] { 26.8467, 80.9462 });
        CITY_COORDS.put("chandigarh", new double[] { 30.7333, 76.7794 });
        CITY_COORDS.put("rajkot", new double[] { 22.3039, 70.8022 });
    }

    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in KM
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double straightKm = R * c;
        return Math.round(straightKm * 1.22 * 10.0) / 10.0;
    }

    @Transactional
    public Order createOrder(User transporter,
            String goodsType,
            Double weight,
            String vehicleType,
            String pickupLocation,
            String dropLocation,
            Double pickupLat,
            Double pickupLng,
            Double dropLat,
            Double dropLng,
            String preferredTime,
            LocalDateTime customPickupTime,
            Order.PaymentMethod paymentMethod,
            Double amount,
            MultipartFile goodsImage,
            String cameraBase64,
            String goodsDescription,
            String contactPersonName,
            String contactPersonPhone,
            Boolean useShipperDetails,
            String pickupDate,
            String pickupTimeSlot,
            Double dimensionLength,
            Double dimensionWidth,
            Double dimensionHeight,
            String dimensionUnit,
            Integer packageCount,
            Boolean isFragile,
            Boolean isHazardous,
            Boolean isTempControlled,
            Double targetTemp,
            Boolean isStackable,
            MultipartFile invoiceFile,
            List<MultipartFile> docFiles,
            String driverNotes) throws IOException {

        Order order = new Order();
        order.setTransporter(transporter);
        order.setGoodsType(goodsType != null ? goodsType : "General Cargo");
        order.setWeight(weight != null ? weight : 1000.0);
        order.setVehicleType(vehicleType != null ? vehicleType : "Standard Truck");
        order.setPickupLocation(pickupLocation);
        order.setDropLocation(dropLocation);
        order.setPreferredTime(preferredTime != null ? preferredTime : "Flexible");
        order.setCustomPickupTime(customPickupTime);
        order.setPaymentMethod(paymentMethod != null ? paymentMethod : Order.PaymentMethod.COD);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        // New Consignment Specifications
        order.setGoodsDescription(goodsDescription);
        order.setContactPersonName(contactPersonName != null && !contactPersonName.isBlank() ? contactPersonName
                : (Boolean.TRUE.equals(useShipperDetails) ? transporter.getName() : ""));
        order.setContactPersonPhone(contactPersonPhone != null && !contactPersonPhone.isBlank() ? contactPersonPhone
                : (Boolean.TRUE.equals(useShipperDetails) ? transporter.getPhone() : ""));
        order.setUseShipperDetails(useShipperDetails != null ? useShipperDetails : false);
        order.setPickupDate(pickupDate != null && !pickupDate.isBlank() ? pickupDate
                : LocalDateTime.now().toLocalDate().toString());
        order.setPickupTimeSlot(
                pickupTimeSlot != null && !pickupTimeSlot.isBlank() ? pickupTimeSlot : "Immediate / Express");
        order.setDimensionLength(dimensionLength);
        order.setDimensionWidth(dimensionWidth);
        order.setDimensionHeight(dimensionHeight);
        order.setDimensionUnit(dimensionUnit != null ? dimensionUnit : "cm");
        order.setPackageCount(packageCount != null && packageCount > 0 ? packageCount : 1);
        order.setIsFragile(isFragile != null ? isFragile : false);
        order.setIsHazardous(isHazardous != null ? isHazardous : false);
        order.setIsTempControlled(isTempControlled != null ? isTempControlled : false);
        order.setTargetTemp(targetTemp);
        order.setIsStackable(isStackable != null ? isStackable : true);
        order.setDriverNotes(driverNotes);

        // Geocoding Coordinates & Distance
        double[] pCoords = lookupCoords(pickupLocation, 19.0760, 72.8777);
        double[] dCoords = lookupCoords(dropLocation, 28.6139, 77.2090);
        double finalPickupLat = pickupLat != null ? pickupLat : (pCoords != null ? pCoords[0] : 19.0760);
        double finalPickupLng = pickupLng != null ? pickupLng : (pCoords != null ? pCoords[1] : 72.8777);
        double finalDropLat = dropLat != null ? dropLat : (dCoords != null ? dCoords[0] : 28.6139);
        double finalDropLng = dropLng != null ? dropLng : (dCoords != null ? dCoords[1] : 77.2090);

        order.setPickupLat(finalPickupLat);
        order.setPickupLng(finalPickupLng);
        order.setDropLat(finalDropLat);
        order.setDropLng(finalDropLng);
        order.setCurrentLat(finalPickupLat);
        order.setCurrentLng(finalPickupLng);

        double distKm = calculateDistanceKm(finalPickupLat, finalPickupLng, finalDropLat, finalDropLng);
        order.setDistanceKm(distKm);

        order.setAmount(amount != null && amount > 0 ? amount : calculateEstimatedFare(weight, distKm));

        // Upload Goods Image (From Camera or File)
        if (cameraBase64 != null && !cameraBase64.trim().isEmpty()) {
            String imgUrl = storageService.uploadBase64Image(cameraBase64, "goods");
            order.setImagePath(imgUrl);
        } else if (goodsImage != null && !goodsImage.isEmpty()) {
            String imgUrl = storageService.uploadFile(goodsImage, "goods");
            order.setImagePath(imgUrl);
        }

        // Upload Invoice
        if (invoiceFile != null && !invoiceFile.isEmpty()) {
            String invoiceUrl = storageService.uploadFile(invoiceFile, "invoices");
            order.setInvoiceUrl(invoiceUrl);
        }

        // Upload additional cargo photos/documents (up to 5)
        if (docFiles != null && !docFiles.isEmpty()) {
            List<String> docUrls = new ArrayList<>();
            for (int i = 0; i < Math.min(docFiles.size(), 5); i++) {
                MultipartFile doc = docFiles.get(i);
                if (doc != null && !doc.isEmpty()) {
                    docUrls.add(storageService.uploadFile(doc, "documents"));
                }
            }
            if (!docUrls.isEmpty()) {
                order.setDocUrls(String.join(",", docUrls));
            }
        }

        Order saved = orderRepository.save(order);

        // Update transporter's totalShippedOrders count
        transporter.setTotalShippedOrders(
                (transporter.getTotalShippedOrders() != null ? transporter.getTotalShippedOrders() : 0) + 1);
        userRepository.save(transporter);

        // Audit Log
        auditLogService.log(
                transporter,
                "CREATED_ORDER",
                "ORDER",
                String.format("Consignment #%d posted (%s -> %s, %s, %.1f km)", saved.getId(), pickupLocation,
                        dropLocation, goodsType, distKm));

        // Notify all drivers of newly posted open order
        List<User> drivers = userRepository.findByRole(User.Role.DRIVER);
        for (User driver : drivers) {
            Notification notif = new Notification(
                    driver,
                    "New Load Available! 📦",
                    String.format("Load #%d (%s, %.1f km) from %s to %s is open for claim.", saved.getId(), goodsType,
                            distKm, pickupLocation, dropLocation),
                    "ORDER",
                    "/driver/orders");
            notificationRepository.save(notif);
        }

        return saved;
    }

    @Transactional
    public boolean rateDriverAndPay(Long orderId, User transporter, Double rating, String review,
            String paymentMethodStr) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty())
            return false;

        Order order = orderOpt.get();
        if (!order.getTransporter().getId().equals(transporter.getId())) {
            return false;
        }
        if (order.getStatus() != Order.OrderStatus.COMPLETED) {
            return false;
        }
        if (order.getPodImageUrl() == null || order.getPodImageUrl().isBlank()) {
            return false;
        }

        if (rating != null && rating >= 1.0 && rating <= 5.0) {
            order.setDriverRating(rating);
            order.setDriverReview(review);

            // Update driver's overall aggregate rating
            User driver = order.getDriver();
            if (driver != null) {
                int priorCount = driver.getRatingCount() != null ? driver.getRatingCount() : 0;
                double currentRating = driver.getRating() != null ? driver.getRating() : 0.0;
                double newRating = priorCount == 0 ? rating
                        : ((currentRating * priorCount) + rating) / (priorCount + 1);
                driver.setRating(Math.round(newRating * 10.0) / 10.0);
                driver.setRatingCount(priorCount + 1);
                userRepository.save(driver);

                // Notify Driver
                Notification notif = new Notification(
                        driver,
                        "⭐ New Rating Received!",
                        String.format("Shipper %s rated you %.1f/5.0 for Order #%d: \"%s\"",
                                transporter.getName(), rating, order.getId(),
                                review != null ? review : "Excellent delivery"),
                        "SUCCESS",
                        "/driver/profile");
                notificationRepository.save(notif);
            }
        }

        order.setPaymentStatus(Order.PaymentStatus.PAID);
        if (paymentMethodStr != null) {
            try {
                order.setPaymentMethod(Order.PaymentMethod.valueOf(paymentMethodStr.toUpperCase()));
            } catch (Exception ignored) {
            }
        }
        orderRepository.save(order);

        String logMsg = rating != null
                ? String.format("Shipper rated Driver %.1f stars and settled payment for Order #%d", rating, order.getId())
                : String.format("Shipper settled payment for Order #%d", order.getId());
        String logAction = rating != null ? "RATED_DRIVER" : "SETTLED_PAYMENT";
        auditLogService.log(transporter, logAction, "SETTLEMENT", logMsg);

        return true;
    }

    @Transactional
    public boolean cancelOrder(Long orderId, User transporter, String reason) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty())
            return false;

        Order order = orderOpt.get();
        if (!order.getTransporter().getId().equals(transporter.getId())) {
            return false;
        }

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            return false;
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancellationReason(reason != null && !reason.trim().isEmpty() ? reason : "Cancelled by transporter");
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);

        Notification notif = new Notification(
                transporter,
                "Order Cancelled",
                "Order #" + order.getId() + " was successfully cancelled.",
                "WARNING",
                "/transporter/my-orders");
        notificationRepository.save(notif);

        auditLogService.log(
                transporter,
                "CANCELLED_ORDER",
                "ORDER",
                "Order #" + order.getId() + " cancelled. Reason: " + reason);

        return true;
    }

    public List<Order> getTransporterOrders(User transporter) {
        return orderRepository.findByTransporterOrderByCreatedAtDesc(transporter);
    }

    @Transactional
    public User updateProfile(User transporter,
            String name,
            String phone,
            String companyName,
            String companyAddress,
            String gstNumber,
            String upiId,
            String avatarUrl,
            String bannerUrl,
            MultipartFile avatar,
            MultipartFile banner,
            MultipartFile upiQr) throws IOException {

        if (name != null && !name.trim().isEmpty())
            transporter.setName(name);
        if (phone != null)
            transporter.setPhone(phone);
        if (companyName != null)
            transporter.setCompanyName(companyName);
        if (companyAddress != null)
            transporter.setCompanyAddress(companyAddress);
        if (gstNumber != null)
            transporter.setGstNumber(gstNumber);
        if (upiId != null)
            transporter.setUpiId(upiId);

        if (avatar != null && !avatar.isEmpty()) {
            transporter.setAvatarUrl(storageService.uploadFile(avatar, "avatars"));
        } else if (avatarUrl != null && !avatarUrl.isBlank()) {
            if (avatarUrl.startsWith("data:image")) {
                transporter.setAvatarUrl(storageService.uploadBase64Image(avatarUrl, "avatars"));
            } else {
                transporter.setAvatarUrl(avatarUrl);
            }
        }

        if (banner != null && !banner.isEmpty()) {
            transporter.setBannerUrl(storageService.uploadFile(banner, "banners"));
        } else if (bannerUrl != null && !bannerUrl.isBlank()) {
            if (bannerUrl.startsWith("data:image")) {
                transporter.setBannerUrl(storageService.uploadBase64Image(bannerUrl, "banners"));
            } else {
                transporter.setBannerUrl(bannerUrl);
            }
        }

        if (upiQr != null && !upiQr.isEmpty()) {
            transporter.setUpiQrUrl(storageService.uploadFile(upiQr, "qr"));
        }

        User updated = userRepository.save(transporter);

        auditLogService.log(
                transporter,
                "UPDATED_PROFILE",
                "PROFILE",
                "Company profile updated");

        return updated;
    }

    private double[] lookupCoords(String location, double defLat, double defLng) {
        if (location == null || location.isBlank())
            return new double[] { defLat, defLng };
        String lower = location.toLowerCase();
        for (Map.Entry<String, double[]> entry : CITY_COORDS.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return new double[] { defLat, defLng };
    }

    private Double calculateEstimatedFare(Double weight, double distKm) {
        double base = 2500.0;
        if (distKm > 0) {
            base += distKm * 18.5;
        }
        if (weight != null) {
            base += (weight / 100.0) * 85.0;
        }
        return Math.round(base * 100.0) / 100.0;
    }
}
