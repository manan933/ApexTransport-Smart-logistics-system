package com.apextransport.controller;

import com.apextransport.entity.User;
import com.apextransport.repository.UserRepository;
import com.apextransport.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" }, allowCredentials = "true")
public class RestAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private com.apextransport.service.StorageService storageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.apextransport.service.AuditLogService auditLogService;

    public static Map<String, Object> formatUser(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("role", user.getRole().name());
        map.put("phone", user.getPhone() != null ? user.getPhone() : "");
        map.put("companyName", user.getCompanyName() != null ? user.getCompanyName() : "");
        map.put("companyAddress", user.getCompanyAddress() != null ? user.getCompanyAddress() : "");
        map.put("gstNumber", user.getGstNumber() != null ? user.getGstNumber() : "");
        map.put("upiId", user.getUpiId() != null ? user.getUpiId() : "");
        map.put("upiQrUrl", user.getUpiQrUrl() != null ? user.getUpiQrUrl() : "");
        map.put("vehicleNumber", user.getVehicleNumber() != null ? user.getVehicleNumber() : "");
        map.put("vehicleType", user.getVehicleType() != null ? user.getVehicleType() : "");
        map.put("additionalVehicles", user.getAdditionalVehicles() != null ? user.getAdditionalVehicles() : "");
        map.put("vehicleModel", user.getVehicleModel() != null ? user.getVehicleModel() : "");
        map.put("vehiclePhotoUrl", user.getVehiclePhotoUrl() != null ? user.getVehiclePhotoUrl() : "");
        map.put("licenseNumber", user.getLicenseNumber() != null ? user.getLicenseNumber() : "");
        map.put("avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "");
        map.put("bannerUrl", user.getBannerUrl() != null ? user.getBannerUrl() : "");
        map.put("rating", user.getRating() != null ? user.getRating() : 5.0);
        map.put("totalDeliveries", user.getTotalDeliveries() != null ? user.getTotalDeliveries() : 0);
        map.put("shipperRating", user.getShipperRating() != null ? user.getShipperRating() : 5.0);
        map.put("totalShippedOrders", user.getTotalShippedOrders() != null ? user.getTotalShippedOrders() : 0);
        map.put("carbonCredits", user.getCarbonCredits());
        map.put("emergencyStatus", user.getEmergencyStatus() != null ? user.getEmergencyStatus() : "");
        map.put("isActive", user.getIsActive() != null ? user.getIsActive() : true);
        return map;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            session.invalidate();
            return ResponseEntity.ok(Map.of("authenticated", false));
        }

        User user = userOpt.get();
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "user", formatUser(user)));
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpSession session) {
        String email = payload.get("email");
        String password = payload.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }

        Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password."));
        }

        User user = userOpt.get();

        if (user.getIsActive() != null && !user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Account has been suspended by system administrator."));
        }

        boolean passwordMatches = false;
        if (user.getPasswordHash() != null) {
            if (user.getPasswordHash().startsWith("$2a$") || user.getPasswordHash().startsWith("$2b$")
                    || user.getPasswordHash().startsWith("$2y$")) {
                passwordMatches = passwordEncoder.matches(password, user.getPasswordHash());
            } else {
                passwordMatches = user.getPasswordHash().equals(password);
                if (passwordMatches) {
                    user.setPasswordHash(passwordEncoder.encode(password));
                    userRepository.save(user);
                }
            }
        }

        if (!passwordMatches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password."));
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole().name());
        session.setAttribute("userName", user.getName());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login successful",
                "user", formatUser(user)));
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload, HttpSession session) {
        String name = payload.get("name");
        String email = payload.get("email");
        String password = payload.get("password");
        String roleStr = payload.get("role");
        String phone = payload.get("phone");
        String companyName = payload.get("companyName");
        String vehicleNumber = payload.get("vehicleNumber");
        String vehicleType = payload.get("vehicleType");
        String licenseNumber = payload.get("licenseNumber");

        if (email == null || password == null || name == null || roleStr == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name, email, password, and role are required."));
        }

        if (userRepository.findByEmail(email.trim().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "An account with this email already exists."));
        }

        User.Role role;
        try {
            role = User.Role.valueOf(roleStr.toUpperCase());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role selected."));
        }
        if (role == User.Role.ADMIN) {
            return ResponseEntity.badRequest().body(Map.of("error", "Admin accounts cannot be self-registered."));
        }

        User newUser = new User();
        newUser.setName(name.trim());
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPasswordHash(passwordEncoder.encode(password));
        newUser.setRole(role);
        newUser.setPhone(phone);
        newUser.setIsActive(true);

        if (role == User.Role.TRANSPORTER) {
            newUser.setCompanyName(companyName);

        } else if (role == User.Role.DRIVER)

        {
            newUser.setVehicleNumber(vehicleNumber);
            newUser.setVehicleType(vehicleType);
            newUser.setLicenseNumber(licenseNumber);

        } else {
            newUser.setAvatarUrl("https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200");
        }

        userRepository.save(newUser);

        session.setAttribute("userId", newUser.getId());
        session.setAttribute("userRole", newUser.getRole().name());
        session.setAttribute("userName", newUser.getName());

        return ResponseEntity.ok(Map.of("success", true, "message", "Registration successful", "user",

                formatUser(newUser)));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
    }

    @PostMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("name") String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "upiId", required = false) String upiId,
            @RequestParam(value = "avatarUrl", required = false) String avatarUrl,
            @RequestParam(value = "bannerUrl", required = false) String bannerUrl,
            @RequestParam(value = "vehicleNumber", required = false) String vehicleNumber,
            @RequestParam(value = "vehicleType", required = false) String vehicleType,
            @RequestParam(value = "additionalVehicles", required = false) String additionalVehicles,
            @RequestParam(value = "licenseNumber", required = false) String licenseNumber,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "gstNumber", required = false) String gstNumber,
            @RequestParam(value = "companyAddress", required = false) String companyAddress,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
        }
        if (phone != null) user.setPhone(phone.trim());
        if (upiId != null) user.setUpiId(upiId.trim());

        try {
            if (avatarUrl != null && !avatarUrl.isBlank()) {
                if (avatarUrl.startsWith("data:image")) {
                    user.setAvatarUrl(storageService.uploadBase64Image(avatarUrl, "avatars"));
                } else {
                    user.setAvatarUrl(avatarUrl);
                }
            }
            if (bannerUrl != null && !bannerUrl.isBlank()) {
                if (bannerUrl.startsWith("data:image")) {
                    user.setBannerUrl(storageService.uploadBase64Image(bannerUrl, "banners"));
                } else {
                    user.setBannerUrl(bannerUrl);
                }
            }
        } catch (Exception e) {
            // ignore upload failure
        }

        if (user.getRole() == User.Role.DRIVER) {
            if (vehicleNumber != null) user.setVehicleNumber(vehicleNumber.trim());
            if (vehicleType != null) user.setVehicleType(vehicleType.trim());
            if (additionalVehicles != null) user.setAdditionalVehicles(additionalVehicles.trim());
            if (licenseNumber != null) user.setLicenseNumber(licenseNumber.trim());
        } else if (user.getRole() == User.Role.TRANSPORTER) {
            if (companyName != null) user.setCompanyName(companyName.trim());
            if (gstNumber != null) user.setGstNumber(gstNumber.trim());
            if (companyAddress != null) user.setCompanyAddress(companyAddress.trim());
        }

        User saved = userRepository.save(user);
        session.setAttribute("userName", saved.getName());

        auditLogService.log(saved, "UPDATED_PROFILE", "PROFILE", "User updated profile settings", "127.0.0.1");

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile updated successfully",
                "user", formatUser(saved)
        ));
    }
}
