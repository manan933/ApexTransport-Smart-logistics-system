package com.apextransport.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tm_orders", indexes = {
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_transporter", columnList = "transporter_id"),
    @Index(name = "idx_order_driver", columnList = "driver_id"),
    @Index(name = "idx_order_created_at", columnList = "createdAt")
})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transporter_id", nullable = false)
    private User transporter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private User driver;

    @Column(nullable = false)
    private String goodsType;

    private Double weight;
    private String vehicleType;

    @Column(nullable = false)
    private String pickupLocation;

    @Column(nullable = false)
    private String dropLocation;

    // Geocoding Coordinates for Map Tracking
    private Double pickupLat;
    private Double pickupLng;
    private Double dropLat;
    private Double dropLng;
    private Double currentLat;
    private Double currentLng;

    private String preferredTime; // e.g. "Morning (9 AM)", "Custom"
    private LocalDateTime customPickupTime;
    private String imagePath;

    // Advanced Consignment Metadata & Specs
    @Column(length = 1000)
    private String goodsDescription;
    private String contactPersonName;
    private String contactPersonPhone;
    private Boolean useShipperDetails = false;
    private String pickupDate;
    private String pickupTimeSlot;
    private Double dimensionLength;
    private Double dimensionWidth;
    private Double dimensionHeight;
    private String dimensionUnit = "cm";
    private Integer packageCount = 1;
    private Boolean isFragile = false;
    private Boolean isHazardous = false;
    private Boolean isTempControlled = false;
    private Double targetTemp;
    private Boolean isStackable = true;
    private Double distanceKm;
    private String invoiceUrl;
    @Column(length = 2000)
    private String docUrls;
    @Column(length = 1000)
    private String driverNotes;
    @Version
    private Long version;
    // Delivery Proof & Ratings
    private String podImageUrl;
    private Double driverRating;
    @Column(length = 500)
    private String driverReview;
    private Double shipperRating;
    @Column(length = 500)
    private String shipperReview;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private Double amount;
    private String cancellationReason;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    public enum OrderStatus {
        PENDING, ACCEPTED, IN_TRANSIT, COMPLETED, CANCELLED
    }

    public enum PaymentMethod {
        COD, UPI
    }

    public enum PaymentStatus {
        PENDING, PAID
    }

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getTransporter() {
        return transporter;
    }

    public void setTransporter(User transporter) {
        this.transporter = transporter;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public Double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(Double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public Double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(Double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public Double getDropLat() {
        return dropLat;
    }

    public void setDropLat(Double dropLat) {
        this.dropLat = dropLat;
    }

    public Double getDropLng() {
        return dropLng;
    }

    public void setDropLng(Double dropLng) {
        this.dropLng = dropLng;
    }

    public Double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(Double currentLat) {
        this.currentLat = currentLat;
    }

    public Double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(Double currentLng) {
        this.currentLng = currentLng;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public LocalDateTime getCustomPickupTime() {
        return customPickupTime;
    }

    public void setCustomPickupTime(LocalDateTime customPickupTime) {
        this.customPickupTime = customPickupTime;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public Boolean getUseShipperDetails() {
        return useShipperDetails;
    }

    public void setUseShipperDetails(Boolean useShipperDetails) {
        this.useShipperDetails = useShipperDetails;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTimeSlot() {
        return pickupTimeSlot;
    }

    public void setPickupTimeSlot(String pickupTimeSlot) {
        this.pickupTimeSlot = pickupTimeSlot;
    }

    public Double getDimensionLength() {
        return dimensionLength;
    }

    public void setDimensionLength(Double dimensionLength) {
        this.dimensionLength = dimensionLength;
    }

    public Double getDimensionWidth() {
        return dimensionWidth;
    }

    public void setDimensionWidth(Double dimensionWidth) {
        this.dimensionWidth = dimensionWidth;
    }

    public Double getDimensionHeight() {
        return dimensionHeight;
    }

    public void setDimensionHeight(Double dimensionHeight) {
        this.dimensionHeight = dimensionHeight;
    }

    public String getDimensionUnit() {
        return dimensionUnit;
    }

    public void setDimensionUnit(String dimensionUnit) {
        this.dimensionUnit = dimensionUnit;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public Boolean getIsFragile() {
        return isFragile;
    }

    public void setIsFragile(Boolean isFragile) {
        this.isFragile = isFragile;
    }

    public Boolean getIsHazardous() {
        return isHazardous;
    }

    public void setIsHazardous(Boolean isHazardous) {
        this.isHazardous = isHazardous;
    }

    public Boolean getIsTempControlled() {
        return isTempControlled;
    }

    public void setIsTempControlled(Boolean isTempControlled) {
        this.isTempControlled = isTempControlled;
    }

    public Double getTargetTemp() {
        return targetTemp;
    }

    public void setTargetTemp(Double targetTemp) {
        this.targetTemp = targetTemp;
    }

    public Boolean getIsStackable() {
        return isStackable;
    }

    public void setIsStackable(Boolean isStackable) {
        this.isStackable = isStackable;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

    public String getDocUrls() {
        return docUrls;
    }

    public void setDocUrls(String docUrls) {
        this.docUrls = docUrls;
    }

    public String getDriverNotes() {
        return driverNotes;
    }

    public void setDriverNotes(String driverNotes) {
        this.driverNotes = driverNotes;
    }

    public String getPodImageUrl() {
        return podImageUrl;
    }

    public void setPodImageUrl(String podImageUrl) {
        this.podImageUrl = podImageUrl;
    }

    public Double getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Double driverRating) {
        this.driverRating = driverRating;
    }

    public String getDriverReview() {
        return driverReview;
    }

    public void setDriverReview(String driverReview) {
        this.driverReview = driverReview;
    }

    public Double getShipperRating() {
        return shipperRating;
    }

    public void setShipperRating(Double shipperRating) {
        this.shipperRating = shipperRating;
    }

    public String getShipperReview() {
        return shipperReview;
    }

    public void setShipperReview(String shipperReview) {
        this.shipperReview = shipperReview;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id != null && id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
