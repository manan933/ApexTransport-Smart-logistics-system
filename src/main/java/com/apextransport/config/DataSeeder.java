package com.apextransport.config;

import com.apextransport.entity.User;
import com.apextransport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        // ==========================================
        // 1. ADMIN USER (1 / 1)
        // ==========================================
        User admin = new User();
        admin.setName("Administrator");
        admin.setEmail("1");
        admin.setPasswordHash(passwordEncoder.encode("1"));
        admin.setRole(User.Role.ADMIN);
        userRepository.save(admin);

        // ==========================================
        // 2. TRANSPORTER USER (2 / 2)
        // ==========================================
        User t1 = new User();
        t1.setName("Shipper");
        t1.setEmail("2");
        t1.setPasswordHash(passwordEncoder.encode("2"));
        t1.setRole(User.Role.TRANSPORTER);
        t1.setShipperRating(5.0);
        t1.setTotalShippedOrders(0);
        userRepository.save(t1);

        // ==========================================
        // 3. DRIVER USER (3 / 3)
        // ==========================================
        User d1 = new User();
        d1.setName("Driver");
        d1.setEmail("3");
        d1.setPasswordHash(passwordEncoder.encode("3"));
        d1.setRole(User.Role.DRIVER);
        d1.setVehicleType("Standard Truck");
        d1.setRating(5.0);
        d1.setTotalDeliveries(0);
        userRepository.save(d1);
    }
}
