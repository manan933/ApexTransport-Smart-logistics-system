package com.apextransport.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.apextransport.config.FirebaseConfig;
import com.apextransport.entity.User;
import com.apextransport.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FirebaseConfig firebaseConfig;

    /**
     * Validates email + password using BCrypt password hashing.
     */
    @Transactional
    public Optional<User> validateLogin(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        String storedHash = user.getPasswordHash();

        // Check with BCrypt
        if (storedHash != null && storedHash.startsWith("$2")) {
            if (passwordEncoder.matches(rawPassword, storedHash)) {
                return Optional.of(user);
            }
        } else if (storedHash != null && storedHash.equals(rawPassword)) {
            // Legacy / demo plaintext password match -> auto upgrade to BCrypt
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            return Optional.of(user);
        }

        return Optional.empty();
    }

    /**
     * Verifies a Firebase ID token sent from the client.
     * Returns the matching or newly created User entity.
     */
    @Transactional
    public Optional<User> verifyAndAuthenticateFirebaseToken(String idToken, String roleHint) {
        try {
            String email = null;
            String name = null;
            String uid = null;
            String picture = null;

            if (!firebaseConfig.isFirebaseInitialized()) {
                log.error("Firebase Admin SDK not initialized — rejecting token login attempt.");
                return Optional.empty();
            }
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            uid = decodedToken.getUid();
            email = decodedToken.getEmail();
            name = decodedToken.getName();
            picture = decodedToken.getPicture();

            if (email == null || email.trim().isEmpty()) {
                return Optional.empty();
            }

            // Find existing user by email or Firebase UID
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                User u = existingUser.get();
                if (uid != null)
                    u.setFirebaseUid(uid);
                if (picture != null && (u.getAvatarUrl() == null || u.getAvatarUrl().isEmpty())) {
                    u.setAvatarUrl(picture);
                }
                userRepository.save(u);
                return Optional.of(u);
            }

            // If user does not exist, create new user account with specified role
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name != null && !name.isEmpty() ? name : email.split("@")[0]);
            newUser.setFirebaseUid(uid);
            newUser.setAvatarUrl(picture);
            newUser.setPasswordHash(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));

            User.Role role = User.Role.TRANSPORTER;
            if (roleHint != null) {
                try {
                    role = User.Role.valueOf(roleHint.toUpperCase());
                } catch (Exception ignored) {
                }
            }
            newUser.setRole(role);
            if (role == User.Role.TRANSPORTER) {
                newUser.setCompanyName(newUser.getName() + " Logistics");
            } else if (role == User.Role.DRIVER) {
                newUser.setVehicleNumber("APEX-" + (1000 + new java.util.Random().nextInt(9000)));
                newUser.setVehicleType("Container Truck");
            }

            userRepository.save(newUser);
            return Optional.of(newUser);

        } catch (Exception e) {
            log.error("Firebase token verification failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

}
