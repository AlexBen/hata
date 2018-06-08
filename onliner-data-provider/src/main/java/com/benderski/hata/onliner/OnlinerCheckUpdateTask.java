package com.benderski.hata.onliner;

import com.benderski.hata.remotedataprovider.ApartmentsStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.logging.Logger;

@Service
public class OnlinerCheckUpdateTask implements Runnable {
    private static Logger LOGGER = Logger.getLogger(OnlinerCheckUpdateTask.class.getName());

    private static int counter = 0;

    @Autowired
    private OnlinerRestClient restClient;
    @Autowired
    private ApartmentsStorage apartmentsStorage;

    @Override
    public void run() {
        try {
            LOGGER.info(OnlinerCheckUpdateTask.class.getName() + " task executed, #" + ++counter);
            OnlinerResponse onlinerResponse = restClient.requestList(Collections.emptyMap());
            apartmentsStorage.addNewPortion(onlinerResponse.getApartments());
        } catch (Exception e) {
            LOGGER.severe(e.getLocalizedMessage());
        }
    }
}
