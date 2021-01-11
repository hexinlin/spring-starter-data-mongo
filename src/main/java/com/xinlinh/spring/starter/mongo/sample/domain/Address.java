package com.xinlinh.spring.starter.mongo.sample.domain;

/**
 * @ClassName: Address
 * @Description: TODO
 * @Author:xinlinh
 * @Date: 2021/1/11 14:07
 * @Version: 1.0
 **/
public class Address {

    private String street;
    private String city;
    private String zip;

    public Address() {
    }

    public Address(String street, String city, String zip) {
        this.street = street;
        this.city = city;
        this.zip = zip;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
