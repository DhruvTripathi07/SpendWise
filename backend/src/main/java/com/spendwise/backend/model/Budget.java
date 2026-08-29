package com.spendwise.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "category", "month" }))
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private BigDecimal amount;

    private String category;

    private LocalDate month;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Budget() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}