package com.benderski.hata.onliner;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OnlinerRestClient {

    private static String baseUrl = "https://ak.api.onliner.by/search/apartments?rent_type%5B%5D=2_rooms&rent_type%5B%5D=3_rooms&rent_type%5B%5D=4_rooms&rent_type%5B%5D=5_rooms&rent_type%5B%5D=6_rooms&only_owner=true&metro%5B%5D=red_line&metro%5B%5D=blue_line&price%5Bmin%5D=285&price%5Bmax%5D=420&currency=usd&bounds%5Blb%5D%5Blat%5D=53.89073372045159&bounds%5Blb%5D%5Blong%5D=27.51259803771973&bounds%5Brt%5D%5Blat%5D=53.92900701725883&bounds%5Brt%5D%5Blong%5D=27.621774673461918&page=1";

    private RestTemplate restTemplate = new RestTemplate();

    public OnlinerResponse requestList(Map<String, ?> params) {
        OnlinerResponse response = restTemplate.getForObject(baseUrl, OnlinerResponse.class, params);
        return response;
    }
}
