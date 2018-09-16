package com.htn.budgetbuddy.models;

import java.util.List;
import java.util.Objects;

public class Transaction {
    private String customerId;
    private double currencyAmount;
    private String merchantName;
    private String description;
    private String categoryCode;
    private List<String> categoryTags;
    private String locationCountry;
    private String locationRegion;
    private String locationCity;
    private String locationStreet;
    private String locationPostalCode;
    private double locationLatitude;
    private double locationLongitude;
    private String originationDateTime;

    public Transaction(String customerId, double currencyAmount, String merchantName, String description, String categoryCode, List<String> categoryTags, String locationCountry, String locationRegion, String locationCity, String locationStreet, String locationPostalCode, double locationLatitude, double locationLongitude, String originationDateTime) {
        this.customerId = customerId;
        this.currencyAmount = currencyAmount;
        this.merchantName = merchantName;
        this.description = description;
        this.categoryCode = categoryCode;
        this.categoryTags = categoryTags;
        this.locationCountry = locationCountry;
        this.locationRegion = locationRegion;
        this.locationCity = locationCity;
        this.locationStreet = locationStreet;
        this.locationPostalCode = locationPostalCode;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.originationDateTime = originationDateTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(double currencyAmount) {
        this.currencyAmount = currencyAmount;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public List<String> getCategoryTags() {
        return categoryTags;
    }

    public void setCategoryTags(List<String> categoryTags) {
        this.categoryTags = categoryTags;
    }

    public String getLocationCountry() {
        return locationCountry;
    }

    public void setLocationCountry(String locationCountry) {
        this.locationCountry = locationCountry;
    }

    public String getLocationRegion() {
        return locationRegion;
    }

    public void setLocationRegion(String locationRegion) {
        this.locationRegion = locationRegion;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getLocationStreet() {
        return locationStreet;
    }

    public void setLocationStreet(String locationStreet) {
        this.locationStreet = locationStreet;
    }

    public String getLocationPostalCode() {
        return locationPostalCode;
    }

    public void setLocationPostalCode(String locationPostalCode) {
        this.locationPostalCode = locationPostalCode;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getOriginationDateTime() {
        return originationDateTime;
    }

    public void setOriginationDateTime(String originationDateTime) {
        this.originationDateTime = originationDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.currencyAmount, currencyAmount) == 0 &&
                Double.compare(that.locationLatitude, locationLatitude) == 0 &&
                Double.compare(that.locationLongitude, locationLongitude) == 0 &&
                Objects.equals(customerId, that.customerId) &&
                Objects.equals(merchantName, that.merchantName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(categoryCode, that.categoryCode) &&
                Objects.equals(categoryTags, that.categoryTags) &&
                Objects.equals(locationCountry, that.locationCountry) &&
                Objects.equals(locationRegion, that.locationRegion) &&
                Objects.equals(locationCity, that.locationCity) &&
                Objects.equals(locationStreet, that.locationStreet) &&
                Objects.equals(locationPostalCode, that.locationPostalCode) &&
                Objects.equals(originationDateTime, that.originationDateTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(customerId, currencyAmount, merchantName, description, categoryCode, categoryTags, locationCountry, locationRegion, locationCity, locationStreet, locationPostalCode, locationLatitude, locationLongitude, originationDateTime);
    }
}
