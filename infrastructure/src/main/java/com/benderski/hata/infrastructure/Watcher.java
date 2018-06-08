package com.benderski.hata.infrastructure;

import io.reactivex.Observer;

public interface Watcher<T> {
    TaskScheduler scheduleTaskWithObserver(Observer<T> observer);
}
