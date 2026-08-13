package com.apextransport.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tm_users", indexes = {
    @Index(name = "idx_user_role", columnList = "role"),
    @Index(name = "idx_user_firebase_uid", columnList = "firebaseUid")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String phone;
    private String avatarUrl;
    private String bannerUrl;
    private String upiId;
    private String upiQrUrl;

    // Driver specific
    private String vehicleNumber;
    private String vehicleModel;
    private String vehicleType;
    @Column(length = 1000)
    private String additionalVehicles; // Comma-separated or list of multi-truck types
    private String vehiclePhotoUrl;
    private String licenseNumber;
    private Integer priority = 1;
    private Boolean turnActive = true;
    private Double rating = 0.0;
    private Integer totalDeliveries = 0;
    private Integer ratingCount = 0;
    private Integer shipperRatingCount = 0;
    private Double carbonCredits = 450.0; // ESG Carbon Offset Credits (kg CO2)
    private String emergencyStatus; // null or "TIRE_PUNCTURE", "ENGINE_BREAKDOWN", "MEDICAL_SOS"
    // Transporter specific
    private String companyName;
    private String companyAddress;
    private String gstNumber;
    private Double shipperRating = 0.0;
    private Integer totalShippedOrders = 0;

    // Auth integration
    private String firebaseUid;
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public enum Role {
        DRIVER, TRANSPORTER, ADMIN
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getUpiQrUrl() {
        return upiQrUrl;
    }

    public void setUpiQrUrl(String upiQrUrl) {
        this.upiQrUrl = upiQrUrl;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehiclePhotoUrl() {
        return vehiclePhotoUrl;
    }

    public void setVehiclePhotoUrl(String vehiclePhotoUrl) {
        this.vehiclePhotoUrl = vehiclePhotoUrl;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getTurnActive() {
        return turnActive;
    }

    public void setTurnActive(Boolean turnActive) {
        this.turnActive = turnActive;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getTotalDeliveries() {
        return totalDeliveries;
    }

    public void setTotalDeliveries(Integer totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getShipperRating() {
        return shipperRating;
    }

    public void setShipperRating(Double shipperRating) {
        this.shipperRating = shipperRating;
    }

    public Integer getTotalShippedOrders() {
        return totalShippedOrders;
    }

    public void setTotalShippedOrders(Integer totalShippedOrders) {
        this.totalShippedOrders = totalShippedOrders;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Integer getShipperRatingCount() {
        return shipperRatingCount;
    }

    public void setShipperRatingCount(Integer shipperRatingCount) {
        this.shipperRatingCount = shipperRatingCount;
    }

    public String getAdditionalVehicles() {
        return additionalVehicles;
    }

    public void setAdditionalVehicles(String additionalVehicles) {
        this.additionalVehicles = additionalVehicles;
    }

    public Double getCarbonCredits() {
        return carbonCredits != null ? carbonCredits : 450.0;
    }

    public void setCarbonCredits(Double carbonCredits) {
        this.carbonCredits = carbonCredits;
    }

    public String getEmergencyStatus() {
        return emergencyStatus;
    }

    public void setEmergencyStatus(String emergencyStatus) {
        this.emergencyStatus = emergencyStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
