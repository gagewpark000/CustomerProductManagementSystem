package com.techelevator.ssgeek.model;

import java.time.LocalDate;
import java.util.Objects;

public class Sale {
    private int saleId;
    private int customerId;
    private LocalDate saleDate;
    private LocalDate shipDate;
    // NOTE: When sales are read, the DAO will join to the customer table and pull the customer name
    // to save an additional lookup later. When you display a sale, you almost always want to display the
    // customer name.
    // You can use this technique for any related field, but customer name should be enough.
    private String customerName;

    public Sale() {
    }

    public Sale(int saleId, int customerId, LocalDate saleDate, LocalDate shipDate, String customerName) {
        this.saleId = saleId;
        this.customerId = customerId;
        this.saleDate = saleDate;
        this.shipDate = shipDate;
        this.customerName = customerName;
    }

    public Sale(int saleId, int customerId, LocalDate saleDate, LocalDate shipDate) {
        this.saleId = saleId;
        this.customerId = customerId;
        this.saleDate = saleDate;
        this.shipDate = shipDate;
    }
    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public LocalDate getShipDate() {
        return shipDate;
    }

    public void setShipDate(LocalDate shipDate) {
        this.shipDate = shipDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sale sale = (Sale) o;
        return saleId == sale.saleId &&
                customerId == sale.customerId &&
                Objects.equals(saleDate, sale.saleDate) &&
                Objects.equals(shipDate, sale.shipDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleId, customerId, saleDate, shipDate);
    }
        @Override
        public String toString () {
            return "Sale{" +
                    "saleId=" + saleId +
                    ", customerId=" + customerId +
                    ", saleDate=" + saleDate +
                    ", shipDate=" + shipDate +
                    ", customerName='" + customerName + '\'' +
                    '}';
        }
    }
