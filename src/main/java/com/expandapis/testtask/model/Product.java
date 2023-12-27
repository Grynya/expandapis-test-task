package com.expandapis.testtask.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate entryDate;
    private String itemCode;
    private String itemName;
    private Integer itemQuantity;
    private String status;

    public Long getId() {
        return id;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public String getStatus() {
        return status;
    }

    public Product setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
        return this;
    }

    public Product setItemCode(String itemCode) {
        this.itemCode = itemCode;
        return this;
    }

    public Product setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public Product setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
        return this;
    }

    public Product setStatus(String status) {
        this.status = status;
        return this;
    }
}
