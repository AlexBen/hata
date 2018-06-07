package com.benderski.hata.remotedataprovider;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
public class ApartmentsStorage {

    private final Deque<Apartment> storage = new ConcurrentLinkedDeque<>();
    private Date newestElementDate = new Date(0);

    public void addNewPortion(@NonNull Collection<Apartment> apartments) {
        synchronized (storage) {
            List<Apartment> newElements =
                    apartments.stream().filter(a -> a.getCreatedAt().after(newestElementDate))
                            .sorted(Comparator.comparing(Apartment::getCreatedAt))
                            .collect(Collectors.toList());
            newElements.forEach(e -> System.out.println(e.getLink()));
            storage.addAll(newElements);
            updateNewestElementDate(newElements);
        }
    }

    private void updateNewestElementDate(List<Apartment> newElements) {
        newestElementDate = getNewestDateOfElement(newElements);
        System.out.println("New last element date " + newestElementDate.getTime());

    }

    private Date getNewestDateOfElement(List<Apartment> newElements) {
        if (newElements == null || newElements.isEmpty()) {
            return newestElementDate;
        }
        Apartment apartment = newElements.get(newElements.size() - 1);
        return apartment.getCreatedAt();
    }

    Date getFreshElementDate() {
        return newestElementDate;
    }

    public @Nullable Apartment getFirstElement() {
        return storage.poll();
    }

}
