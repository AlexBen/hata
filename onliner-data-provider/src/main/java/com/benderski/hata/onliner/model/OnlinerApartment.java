package com.benderski.hata.onliner.model;


import com.benderski.hata.infrastructure.Apartment;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class OnlinerApartment implements Apartment {

    private static String LINK_TEMPLATE = "https://r.onliner.by/ak/apartments/";

    private Long id;
    private Price price;
    private Contact contact;
    private Location location;
    private String rent_type;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date created_at;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date last_time_up;

    private String photo;

    @Override
    public Date getCreatedAt() {
        return created_at;
    }

    public String getLink() {
        return LINK_TEMPLATE + id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setRent_type(String rent_type) {
        this.rent_type = rent_type;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setLast_time_up(Date last_time_up) {
        this.last_time_up = last_time_up;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
