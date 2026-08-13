package com.apextransport.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tm_skipped_orders",
       uniqueConstraints = @UniqueConstraint(columnNames = {"driver_id", "order_id"}),
       indexes = {@Index(name = "idx_skipped_driver", columnList = "driver_id")})
 public class SkippedOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public SkippedOrder() {}

    public SkippedOrder(User driver, Order order) {
        this.driver = driver;
        this.order = order;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkippedOrder that = (SkippedOrder) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
