package com.benderski.hata.remotedataprovider;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApartmentsStorageTest {

    private static long counter = 1;

    ApartmentsStorage storage = new ApartmentsStorage();

    @org.junit.jupiter.api.Test
    void addNewPortion() {
        storage.addNewPortion(getTestData());
        storage.addNewPortion(getTestData());
        assertNotNull(storage.getFirstElement());
        assertNotNull(storage.getFirstElement());
        assertNotNull(storage.getFirstElement());
        assertNull(storage.getFirstElement());
        assertTrue(storage.getFreshElementDate().before(new Date()));
    }

    @org.junit.jupiter.api.Test
    void getLastElementDate() {

    }

    private List<Apartment> getTestData() {
        List<Apartment> data = new ArrayList<>();
        data.add(create(counter++, Date.from(Instant.now())));
        data.add(create(counter++, Date.from(Instant.now().minus(1, ChronoUnit.MINUTES))));
        return data;

    }

    private Apartment create(Long id, Date createdAt) {
        ApartmentImpl apartment = new ApartmentImpl();
        apartment.id = id;
        apartment.createAt = createdAt;
        return apartment;
    }

    private class ApartmentImpl implements Apartment {

        Long id;
        Date createAt;

        @Override
        public Date getCreatedAt() {
            return createAt;
        }

        @Override
        public String getLink() {
            return id.toString();
        }
    }

}