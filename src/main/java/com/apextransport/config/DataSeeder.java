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
        // 1. ADMIN USER
        // ==========================================
        User admin = new User();
        admin.setName("Apex Admin");
        admin.setEmail("admin@apex.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ADMIN);
        admin.setPhone("+91 98765 43210");
        admin.setAvatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80");
        admin.setBannerUrl("https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?w=1200&auto=format&fit=crop&q=80");
        userRepository.save(admin);

        // ==========================================
        // 2. TRANSPORTERS
        // ==========================================
        User t1 = new User();
        t1.setName("Vikram Sharma");
        t1.setEmail("vikram@transporter.com");
        t1.setPasswordHash(passwordEncoder.encode("vikram123"));
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

        User t2 = new User();
        t2.setName("Priya Patel");
        t2.setEmail("priya@transporter.com");
        t2.setPasswordHash(passwordEncoder.encode("priya123"));
        t2.setRole(User.Role.TRANSPORTER);
        t2.setCompanyName("National Express Cargo");
        t2.setCompanyAddress("Sector 18, Gurugram, Delhi NCR");
        t2.setGstNumber("07AAECN4321C1Z2");
        t2.setPhone("+91 99102 54321");
        t2.setUpiId("nationalcargo@okaxis");
        t2.setShipperRating(5.0);
        t2.setTotalShippedOrders(18);
        t2.setAvatarUrl("https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop&q=80");
        t2.setBannerUrl("https://images.unsplash.com/photo-1519003722824-194d4455a60c?w=1200&auto=format&fit=crop&q=80");
        userRepository.save(t2);

        // ==========================================
        // 3. DRIVERS
        // ==========================================
        User d1 = new User();
        d1.setName("Rajesh Singh");
        d1.setEmail("rajesh@driver.com");
        d1.setPasswordHash(passwordEncoder.encode("rajesh123"));
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

        User d2 = new User();
        d2.setName("Sunil Yadav");
        d2.setEmail("sunil@driver.com");
        d2.setPasswordHash(passwordEncoder.encode("sunil123"));
        d2.setRole(User.Role.DRIVER);
        d2.setVehicleNumber("DL 01 AB 9921");
        d2.setVehicleModel("Ashok Leyland 4220 Ecomet");
        d2.setVehicleType("Open Body Truck (24 Ft)");
        d2.setLicenseNumber("DL-DL01201900431");
        d2.setPhone("+91 98112 34567");
        d2.setUpiId("sunilyadav@paytm");
        d2.setPriority(2);
        d2.setTurnActive(true);
        d2.setRating(4.8);
        d2.setTotalDeliveries(32);
        d2.setAvatarUrl("https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&auto=format&fit=crop&q=80");
        d2.setBannerUrl("https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?w=1200&auto=format&fit=crop&q=80");
        userRepository.save(d2);

        User d3 = new User();
        d3.setName("Amit Kumar");
        d3.setEmail("amit@driver.com");
        d3.setPasswordHash(passwordEncoder.encode("amit123"));
        d3.setRole(User.Role.DRIVER);
        d3.setVehicleNumber("KA 05 CD 3341");
        d3.setVehicleModel("BharatBenz 2823C Tipper");
        d3.setVehicleType("Refrigerated Truck (Cold Chain)");
        d3.setLicenseNumber("DL-KA05202000871");
        d3.setPhone("+91 94480 12345");
        d3.setUpiId("amitkumar@okaxis");
        d3.setPriority(3);
        d3.setTurnActive(true);
        d3.setRating(5.0);
        d3.setTotalDeliveries(64);
        d3.setAvatarUrl("https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=150&auto=format&fit=crop&q=80");
        d3.setBannerUrl("https://images.unsplash.com/photo-1601584115197-04ecc0da31d7?w=1200&auto=format&fit=crop&q=80");
        userRepository.save(d3);

        // ==========================================
        // 4. SAMPLE ORDERS
        // ==========================================
        // Order 1: Pending (Available on Open Board)
        Order o1 = new Order();
        o1.setTransporter(t1);
        o1.setGoodsType("Industrial Electronics & Sensors");
        o1.setGoodsDescription("High-precision automation PLCs and industrial sensors packed in antistatic shock-resistant crates.");
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

        // Order 2: In Transit (Assigned to Rajesh)
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

        // Order 3: Completed with POD & Ratings
        Order o3 = new Order();
        o3.setTransporter(t2);
        o3.setDriver(d2);
        o3.setGoodsType("Pharmaceutical Vaccines (Cold Chain)");
        o3.setGoodsDescription("Temperature-monitored biopharma vials stored under -20°C active refrigeration.");
        o3.setContactPersonName("Dr. Swati Sen");
        o3.setContactPersonPhone("+91 94370 88990");
        o3.setPickupDate("2026-07-31");
        o3.setPickupTimeSlot("Morning (8 AM - 10 AM)");
        o3.setDimensionLength(250.0);
        o3.setDimensionWidth(150.0);
        o3.setDimensionHeight(140.0);
        o3.setDimensionUnit("cm");
        o3.setPackageCount(8);
        o3.setIsFragile(true);
        o3.setIsHazardous(false);
        o3.setIsTempControlled(true);
        o3.setTargetTemp(-20.0);
        o3.setIsStackable(false);
        o3.setDistanceKm(1040.0);
        o3.setWeight(950.0);
        o3.setVehicleType("Refrigerated Truck (Cold Chain)");
        o3.setPickupLocation("Genome Valley, Hyderabad");
        o3.setDropLocation("Bhubaneswar AIIMS Hub, Odisha");
        o3.setPickupLat(17.3850);
        o3.setPickupLng(78.4867);
        o3.setDropLat(20.2961);
        o3.setDropLng(85.8245);
        o3.setCurrentLat(20.2961);
        o3.setCurrentLng(85.8245);
        o3.setPreferredTime("Custom");
        o3.setCustomPickupTime(LocalDateTime.now().minusDays(2).withHour(10).withMinute(0));
        o3.setPaymentMethod(Order.PaymentMethod.UPI);
        o3.setPaymentStatus(Order.PaymentStatus.PAID);
        o3.setAmount(31200.0);
        o3.setStatus(Order.OrderStatus.COMPLETED);
        o3.setImagePath("https://images.unsplash.com/photo-1587293852726-70cdb56c2866?w=600&auto=format&fit=crop&q=80");
        o3.setPodImageUrl("https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=600&auto=format&fit=crop&q=80");
        o3.setDriverRating(5.0);
        o3.setDriverReview("Exceptional driver! Maintained cold chain temperature accurately and delivered 2 hours ahead of schedule.");
        o3.setShipperRating(5.0);
        o3.setShipperReview("Great warehouse staff, prompt loading, temperature logging sheets provided immediately.");
        o3.setCreatedAt(LocalDateTime.now().minusDays(3));
        o3.setAcceptedAt(LocalDateTime.now().minusDays(3).plusHours(1));
        o3.setStartedAt(LocalDateTime.now().minusDays(3).plusHours(3));
        o3.setCompletedAt(LocalDateTime.now().minusDays(1));
        orderRepository.save(o3);

        // Seed initial audit log entries
        auditLogRepository.save(new AuditLog(admin, "SYSTEM_INIT", "SYSTEM", "Apex Transport Command Center Initialized", "127.0.0.1"));
        auditLogRepository.save(new AuditLog(t1, "CREATED_ORDER", "ORDER", "Consignment #1 posted (Mumbai -> New Delhi)", "127.0.0.1"));
        auditLogRepository.save(new AuditLog(d1, "CLAIMED_ORDER", "ORDER", "Driver claimed Consignment #2 (Surat -> Bengaluru)", "127.0.0.1"));
        auditLogRepository.save(new AuditLog(d2, "COMPLETED_DELIVERY", "ORDER", "Delivery completed for Order #3 (Hyderabad -> Bhubaneswar). POD Verified.", "127.0.0.1"));

        System.out.println("✅ Apex Transport Data Seeded Successfully with BCrypt Passwords, Full Consignment Specs & Audit Logs!");
    }
}
