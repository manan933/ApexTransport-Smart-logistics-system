package com.apextransport.controller;

import com.apextransport.entity.Order;
import com.apextransport.entity.User;
import com.apextransport.repository.OrderRepository;
import com.apextransport.repository.UserRepository;
import com.apextransport.service.AdminService;
import com.apextransport.service.StorageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" }, allowCredentials = "true")
public class RestAdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private com.apextransport.service.AuditLogService auditLogService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User getLoggedInAdmin(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return null;
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getRole() == User.Role.ADMIN) {
            return user;
        }
        return null;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpSession session) {
        User admin = getLoggedInAdmin(session);
        if (admin == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        Map<String, Object> stats = new HashMap<>(adminService.getSystemStats());
        List<Order> recentOrders = orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .limit(10)
                .toList();

        stats.put("recentOrders", recentOrders.stream().map(RestTransporterController::formatOrder).toList());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(HttpSession session) {
        User admin = getLoggedInAdmin(session);
        if (admin == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        List<Order> list = adminService.getAllOrders();
        return ResponseEntity.ok(list.stream().map(RestTransporterController::formatOrder).toList());
    }

    @GetMapping("/drivers")
    public ResponseEntity<?> getAllDrivers(HttpSession session) {
        User admin = getLoggedInAdmin(session);
        if (admin == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        List<User> list = adminService.getAllDrivers();
        return ResponseEntity.ok(list.stream().map(RestAuthController::formatUser).toList());
    }

    @GetMapping("/transporters")
    public ResponseEntity<?> getAllTransporters(HttpSession session) {
        User admin = getLoggedInAdmin(session);
        if (admin == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        List<User> list = adminService.getAllTransporters();
        return ResponseEntity.ok(list.stream().map(RestAuthController::formatUser).toList());
    }

    @PostMapping("/users/create")
    public ResponseEntity<?> createUser(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String roleStr,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "vehicleNumber", required = false) String vehicleNumber,
            @RequestParam(value = "vehicleType", required = false) String vehicleType,
            @RequestParam(value = "licenseNumber", required = false) String licenseNumber,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            HttpSession session) {

        User admin = getLoggedInAdmin(session);
        if (admin == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        if (userRepository.findByEmail(email.trim().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "An account with this email already exists"));
        }

        try {
            User.Role role = User.Role.valueOf(roleStr.toUpperCase());
            if (role == User.Role.ADMIN) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot create admin accounts"));
            }

            User u = new User();
            u.setName(name.trim());
            u.setEmail(email.trim().toLowerCase());
            u.setPasswordHash(passwordEncoder.encode(password));
            u.setRole(role);
            u.setPhone(phone);
            u.setIsActive(true);

            if (role == User.Role.TRANSPORTER) {
                u.setCompanyName(companyName != null && !companyName.isBlank() ? companyName : name + " Logistics");
                u.setAvatarUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200");
            } else if (role == User.Role.DRIVER) {
                u.setVehicleNumber(vehicleNumber != null ? vehicleNumber : "MH-12-TX-1001");
                u.setVehicleType(vehicleType != null ? vehicleType : "Heavy Freight Trailer");
                u.setLicenseNumber(licenseNumber != null ? licenseNumber : "DL-11928392");
                u.setAvatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200");
            } else {
                u.setAvatarUrl("https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200");
            }

            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarUrl = storageService.uploadFile(avatarFile, "avatars");
                u.setAvatarUrl(avatarUrl);
            }

            userRepository.save(u);
            return ResponseEntity.ok(Map.of("success", true, "message", "User created successfully", "user",
                    RestAuthController.formatUser(u)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/users/{id}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id, HttpSession session) {
        User admin = getLoggedInAdmin(session);
        if (admin == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        boolean newStatus = adminService.toggleUserStatus(id);
        return ResponseEntity.ok(Map.of("success", true, "isActive", newStatus));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpSession session) {
        User admin = getLoggedInAdmin(session);
        if (admin == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        if (id.equals(admin.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "You cannot delete your own admin account"));
        }

        boolean deleted = adminService.deleteUser(id);
        if (!deleted) {
            return ResponseEntity.badRequest().body(Map.of("error", "User cannot be deleted (user not found or has active linked orders)."));
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "User deleted successfully"));
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(HttpSession session) {
        User admin = getLoggedInAdmin(session);
        if (admin == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));

        return ResponseEntity.ok(auditLogService.getAllLogs());
    }
}
