package com.benderski.hata.onliner;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OnlinerRestClient {

    private static String baseUrl = "https://ak.api.onliner.by/search/apartments?rent_type%5B%5D=1_room&rent_type%5B%5D=2_rooms&rent_type%5B%5D=3_rooms&rent_type%5B%5D=4_rooms&rent_type%5B%5D=5_rooms&rent_type%5B%5D=6_rooms&only_owner=true&bounds%5Blb%5D%5Blat%5D=53.8115987186118&bounds%5Blb%5D%5Blong%5D=27.33055114746094&bounds%5Brt%5D%5Blat%5D=53.98476149280952&bounds%5Brt%5D%5Blong%5D=27.79403686523438&_=0.7310359780868383";

    private RestTemplate restTemplate = new RestTemplate();

    public OnlinerResponse requestList(Map<String, ?> params) {
        OnlinerResponse response = restTemplate.getForObject(baseUrl, OnlinerResponse.class, params);
        return response;
    }
}
