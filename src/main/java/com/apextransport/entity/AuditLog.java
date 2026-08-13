package com.apextransport.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tm_audit_logs", indexes = {
    @Index(name = "idx_audit_user", columnList = "user_id"),
    @Index(name = "idx_audit_created_at", columnList = "createdAt"),
    @Index(name = "idx_audit_role", columnList = "role")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private String userEmail;
    private String userName;
    private String role;

    @Column(nullable = false)
    private String action;

    private String category; // e.g. ORDER, AUTH, PROFILE, SYSTEM, SETTLEMENT, RATING

    @Column(length = 2000)
    private String details;

    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public AuditLog() {}

    public AuditLog(User user, String action, String category, String details, String ipAddress) {
        this.user = user;
        if (user != null) {
            this.userEmail = user.getEmail();
            this.userName = user.getName();
            this.role = user.getRole() != null ? user.getRole().name() : "GUEST";
        }
        this.action = action;
        this.category = category;
        this.details = details;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return id != null && id.equals(auditLog.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
