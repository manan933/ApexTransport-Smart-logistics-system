package com.apextransport.config;

import com.apextransport.entity.AuditLog;
import com.apextransport.entity.Order;
import com.apextransport.entity.User;
import com.apextransport.repository.AuditLogRepository;
import com.apextransport.repository.OrderRepository;
import com.apextransport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        // ==========================================
        // 1. ADMIN USER (ID / Password: 1)
        // ==========================================
        User admin = new User();
        admin.setName("Apex System Admin");
        admin.setEmail("1");
        admin.setPasswordHash(passwordEncoder.encode("1"));
        admin.setRole(User.Role.ADMIN);
        admin.setPhone("+91 98765 43210");
        admin.setAvatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80");
        admin.setBannerUrl("https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?w=1200&auto=format&fit=crop&q=80");
        userRepository.save(admin);

        // ==========================================
        // 2. TRANSPORTER USER (ID / Password: 2)
        // ==========================================
        User t1 = new User();
        t1.setName("Vikram Sharma");
        t1.setEmail("2");
        t1.setPasswordHash(passwordEncoder.encode("2"));
        t1.setRole(User.Role.TRANSPORTER);
        t1.setCompanyName("Sharma Freight & Logistics Ltd");
        t1.setCompanyAddress("Plot 42, Transport Nagar, Mumbai, MH");
        t1.setGstNumber("27AABCS1429B1Z8");
        t1.setPhone("+91 98201 12345");
        t1.setUpiId("sharmalogistics@icici");
        t1.setShipperRating(4.9);
        t1.setTotalShippedOrders(24);
        t1.setAvatarUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80");
        t1.setBannerUrl("https://images.unsplash.com/photo-1601584115197-04ecc0da31d7?w=1200&auto=format&fit=crop&q=80");
        userRepository.save(t1);

        // ==========================================
        // 3. DRIVER USER (ID / Password: 3)
        // ==========================================
        User d1 = new User();
        d1.setName("Rajesh Singh");
        d1.setEmail("3");
        d1.setPasswordHash(passwordEncoder.encode("3"));
        d1.setRole(User.Role.DRIVER);
        d1.setVehicleNumber("MH 04 AZ 4589");
        d1.setVehicleModel("Tata Prima 5530.S Heavy Truck");
        d1.setVehicleType("Container Truck (32 Ft)");
        d1.setLicenseNumber("DL-MH04201800984");
        d1.setPhone("+91 97654 32190");
        d1.setUpiId("rajesh.driver@upi");
        d1.setPriority(1);
        d1.setTurnActive(true);
        d1.setRating(4.9);
        d1.setTotalDeliveries(48);
        d1.setAvatarUrl("https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80");
        d1.setBannerUrl("https://images.unsplash.com/photo-1559297434-fae8a1916a79?w=1200&auto=format&fit=crop&q=80");
        userRepository.save(d1);

        // ==========================================
        // 4. SAMPLE ORDERS
        // ==========================================
        Order o1 = new Order();
        o1.setTransporter(t1);
        o1.setGoodsType("Industrial Electronics & Sensors");
        o1.setGoodsDescription("High-precision automation PLCs and industrial sensors packed in antistatic crates.");
        o1.setContactPersonName("Mr. Alok Verma");
        o1.setContactPersonPhone("+91 98200 44321");
        o1.setPickupDate("2026-08-04");
        o1.setPickupTimeSlot("Morning (9 AM - 12 PM)");
        o1.setDimensionLength(320.0);
        o1.setDimensionWidth(180.0);
        o1.setDimensionHeight(160.0);
        o1.setDimensionUnit("cm");
        o1.setPackageCount(14);
        o1.setIsFragile(true);
        o1.setIsHazardous(false);
        o1.setIsTempControlled(false);
        o1.setIsStackable(false);
        o1.setDistanceKm(1415.0);
        o1.setWeight(2400.0);
        o1.setVehicleType("Container Truck (32 Ft)");
        o1.setPickupLocation("Mumbai Port Terminal 2, Mumbai");
        o1.setDropLocation("Nehru Place Logistics Park, New Delhi");
        o1.setPickupLat(19.0760);
        o1.setPickupLng(72.8777);
        o1.setDropLat(28.6139);
        o1.setDropLng(77.2090);
        o1.setCurrentLat(19.0760);
        o1.setCurrentLng(72.8777);
        o1.setPreferredTime("Morning (9 AM)");
        o1.setPaymentMethod(Order.PaymentMethod.UPI);
        o1.setPaymentStatus(Order.PaymentStatus.PAID);
        o1.setAmount(18500.0);
        o1.setStatus(Order.OrderStatus.PENDING);
        o1.setImagePath("https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?w=600&auto=format&fit=crop&q=80");
        o1.setDriverNotes("Please handle cargo with air-cushioned straps. Gate passes are cleared.");
        o1.setCreatedAt(LocalDateTime.now().minusHours(2));
        orderRepository.save(o1);

        Order o2 = new Order();
        o2.setTransporter(t1);
        o2.setDriver(d1);
        o2.setGoodsType("Textiles & Finished Garments");
        o2.setGoodsDescription("Rolls of premium organic cotton & dyed fabrics boxed in weather-shield pallets.");
        o2.setContactPersonName("Kishore Mehta");
        o2.setContactPersonPhone("+91 98980 11223");
        o2.setPickupDate("2026-08-02");
        o2.setPickupTimeSlot("Afternoon (1 PM - 4 PM)");
        o2.setDimensionLength(400.0);
        o2.setDimensionWidth(210.0);
        o2.setDimensionHeight(190.0);
        o2.setDimensionUnit("cm");
        o2.setPackageCount(40);
        o2.setIsFragile(false);
        o2.setIsHazardous(false);
        o2.setIsTempControlled(false);
        o2.setIsStackable(true);
        o2.setDistanceKm(1280.0);
        o2.setWeight(1800.0);
        o2.setVehicleType("Container Truck (32 Ft)");
        o2.setPickupLocation("Surat Textile Hub, Gujarat");
        o2.setDropLocation("Whitefield EPIP Zone, Bengaluru");
        o2.setPickupLat(21.1702);
        o2.setPickupLng(72.8311);
        o2.setDropLat(12.9716);
        o2.setDropLng(77.5946);
        o2.setCurrentLat(17.0709);
        o2.setCurrentLng(75.2128);
        o2.setPreferredTime("Flexible");
        o2.setPaymentMethod(Order.PaymentMethod.COD);
        o2.setPaymentStatus(Order.PaymentStatus.PENDING);
        o2.setAmount(24000.0);
        o2.setStatus(Order.OrderStatus.IN_TRANSIT);
        o2.setImagePath("https://images.unsplash.com/photo-1553413077-190dd305871c?w=600&auto=format&fit=crop&q=80");
        o2.setDriverNotes("Express lane preferred. Unloading bay 4 at Bengaluru depot.");
        o2.setCreatedAt(LocalDateTime.now().minusDays(1));
        o2.setAcceptedAt(LocalDateTime.now().minusHours(18));
        o2.setStartedAt(LocalDateTime.now().minusHours(12));
        orderRepository.save(o2);

        // Audit log init
        auditLogRepository.save(new AuditLog(admin, "SYSTEM_INIT", "SYSTEM", "Apex Transport Command Center Initialized", "127.0.0.1"));
        auditLogRepository.save(new AuditLog(t1, "CREATED_ORDER", "ORDER", "Consignment #1 posted (Mumbai -> New Delhi)", "127.0.0.1"));
        auditLogRepository.save(new AuditLog(d1, "CLAIMED_ORDER", "ORDER", "Driver claimed Consignment #2 (Surat -> Bengaluru)", "127.0.0.1"));

        System.out.println("✅ Apex Transport Accounts Initialized: Admin (1/1), Transporter (2/2), Driver (3/3)!");
    }
}
