package com.benderski.hata.subscription;

import java.util.Date;

public class SubscriptionModel {

    private PropertyType propertyType = PropertyType.FLAT;
    private Integer minPrice;
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
}
