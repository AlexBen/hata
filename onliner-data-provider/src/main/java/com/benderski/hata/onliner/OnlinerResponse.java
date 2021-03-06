package com.benderski.hata.onliner;


import com.benderski.hata.onliner.model.OnlinerApartment;
import com.benderski.hata.onliner.model.Page;
import com.benderski.hata.infrastructure.Apartment;
import com.benderski.hata.infrastructure.RemoteDataResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OnlinerResponse implements RemoteDataResponse {

    public List<OnlinerApartment> apartments = Collections.emptyList();
    public Page page;
    public Integer total;

    @Override
    public Collection<Apartment> getApartments() {
        return apartments.stream().map(Apartment.class::cast).collect(Collectors.toList());
    }


}
