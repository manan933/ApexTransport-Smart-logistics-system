package com.apextransport.service;

import com.apextransport.entity.Order;
import com.apextransport.entity.User;
import com.apextransport.repository.OrderRepository;
import com.apextransport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StorageService storageService;

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<User> getAllDrivers() {
        return userRepository.findByRoleOrderByCreatedAtDesc(User.Role.DRIVER);
    }

    public List<User> getAllTransporters() {
        return userRepository.findByRoleOrderByCreatedAtDesc(User.Role.TRANSPORTER);
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        long acceptedOrders = orderRepository.countByStatus(Order.OrderStatus.ACCEPTED);
        long inTransitOrders = orderRepository.countByStatus(Order.OrderStatus.IN_TRANSIT);
        long activeOrders = acceptedOrders + inTransitOrders;
        long completedOrders = orderRepository.countByStatus(Order.OrderStatus.COMPLETED);
        long cancelledOrders = orderRepository.countByStatus(Order.OrderStatus.CANCELLED);
        long totalDrivers = userRepository.countByRole(User.Role.DRIVER);
        long totalTransporters = userRepository.countByRole(User.Role.TRANSPORTER);

        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("acceptedOrders", acceptedOrders);
        stats.put("inTransitOrders", inTransitOrders);
        stats.put("activeOrders", activeOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("cancelledOrders", cancelledOrders);
        stats.put("totalDrivers", totalDrivers);
        stats.put("driverCount", totalDrivers);
        stats.put("totalTransporters", totalTransporters);
        stats.put("transporterCount", totalTransporters);
        return stats;
    }

    @Transactional
    public User registerDriver(String name,
                               String email,
                               String password,
                               String phone,
                               String vehicleNumber,
                               String vehicleModel,
                               String vehicleType,
                               String licenseNumber,
                               MultipartFile vehiclePhoto) throws IOException {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email is already registered: " + email);
        }

        User driver = new User();
        driver.setName(name);
        driver.setEmail(email);
        driver.setPasswordHash(passwordEncoder.encode(password));
        driver.setRole(User.Role.DRIVER);
        driver.setPhone(phone);
        driver.setVehicleNumber(vehicleNumber);
        driver.setVehicleModel(vehicleModel != null ? vehicleModel : "Heavy Truck");
        driver.setVehicleType(vehicleType != null ? vehicleType : "Container");
        driver.setLicenseNumber(licenseNumber);
        driver.setTurnActive(true);
        driver.setIsActive(true);
        driver.setCreatedAt(LocalDateTime.now());

        if (vehiclePhoto != null && !vehiclePhoto.isEmpty()) {
            driver.setVehiclePhotoUrl(storageService.uploadFile(vehiclePhoto, "vehicles"));
        }

        return userRepository.save(driver);
    }

    @Transactional
    public User registerTransporter(String name,
                                    String email,
                                    String password,
                                    String phone,
                                    String companyName,
                                    String companyAddress,
                                    String gstNumber) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email is already registered: " + email);
        }

        User transporter = new User();
        transporter.setName(name);
        transporter.setEmail(email);
        transporter.setPasswordHash(passwordEncoder.encode(password));
        transporter.setRole(User.Role.TRANSPORTER);
        transporter.setPhone(phone);
        transporter.setCompanyName(companyName);
        transporter.setCompanyAddress(companyAddress);
        transporter.setGstNumber(gstNumber);
        transporter.setIsActive(true);
        transporter.setCreatedAt(LocalDateTime.now());

        return userRepository.save(transporter);
    }

    @Transactional
    public Boolean toggleUserStatus(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            boolean newStatus = u.getIsActive() == null || !u.getIsActive();
            u.setIsActive(newStatus);
            userRepository.save(u);
            return newStatus;
        }
        return null;
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) return false;
        try {
            userRepository.deleteById(userId);
            userRepository.flush();
            return true;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return false;
        }
    }
}
