package com.benderski.hata.infrastructure;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ShutdownSignal {
    private final AtomicBoolean started = new AtomicBoolean(true);

    public void stop() {
        started.set(false);
    }

    public boolean isStopped() {
        return !started.get();
    }
}
