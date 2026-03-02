package com.airline.conf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;

@Singleton
public class PersistenceLifecycle {

    private final PersistService persistService;

    @Inject
    public PersistenceLifecycle(PersistService persistService) {
        this.persistService = persistService;
    }

    @Start
    public void startPersistence() {
        persistService.start();
    }

    @Dispose
    public void stopPersistence() {
        persistService.stop();
    }
}
