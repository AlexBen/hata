package com.benderski.hata.subscription;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.validation.constraints.NotNull;

public class SubscriptionModel implements Serializable {

    private PropertyType propertyType = PropertyType.FLAT;

    @NotNull
    private Integer minPrice;
    @NotNull
    private Integer maxPrice;
    private Date subscriptionCreatedDate = new Date();
    private Date subscriptionStartedDate;
    private Integer minNumberOfRooms;
    private Integer maxNumberOfRooms;
    private Boolean ownerOnly;

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Date getSubscriptionCreatedDate() {
        return subscriptionCreatedDate;
    }

    public Date getSubscriptionStartedDate() {
        return subscriptionStartedDate;
    }

    public void setSubscriptionStartedDate(Date subscriptionStartedDate) {
        this.subscriptionStartedDate = subscriptionStartedDate;
    }

    public Integer getMinNumberOfRooms() {
        return minNumberOfRooms;
    }

    public void setMinNumberOfRooms(Integer minNumberOfRooms) {
        this.minNumberOfRooms = minNumberOfRooms;
    }

    public Integer getMaxNumberOfRooms() {
        return maxNumberOfRooms;
    }

    public void setMaxNumberOfRooms(Integer maxNumberOfRooms) {
        this.maxNumberOfRooms = maxNumberOfRooms;
    }

    public Boolean getOwnerOnly() {
        return ownerOnly;
    }

    public void setOwnerOnly(Boolean ownerOnly) {
        this.ownerOnly = ownerOnly;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionModel that = (SubscriptionModel) o;
        return propertyType == that.propertyType &&
                Objects.equals(minPrice, that.minPrice) &&
                Objects.equals(maxPrice, that.maxPrice) &&
                Objects.equals(subscriptionCreatedDate, that.subscriptionCreatedDate) &&
                Objects.equals(subscriptionStartedDate, that.subscriptionStartedDate) &&
                Objects.equals(minNumberOfRooms, that.minNumberOfRooms) &&
                Objects.equals(maxNumberOfRooms, that.maxNumberOfRooms) &&
                Objects.equals(ownerOnly, that.ownerOnly);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyType, minPrice, maxPrice, subscriptionCreatedDate, subscriptionStartedDate, minNumberOfRooms, maxNumberOfRooms, ownerOnly);
    }

    @Override
    public String toString() {
        return "минимальная цена, usd: " + minPrice +
                "\nмаксимальная цена, usd: " + maxPrice +
                "\nминимальное количество комнат: " + minNumberOfRooms +
                "\nподписка активна: " + (subscriptionStartedDate != null ? "да" : "нет");
    }
}
