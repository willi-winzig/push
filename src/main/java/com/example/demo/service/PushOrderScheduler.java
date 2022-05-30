package com.example.demo.service;

import com.example.demo.entity.PushDevice;
import com.example.demo.entity.PushOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
// @EnableAsync
public class PushOrderScheduler {

    @Autowired
    private PushOrderRepository pushOrderRepository;

    @Autowired
    private PushDeviceRepository pushDeviceRepository;

    @Autowired
    private PropertyService propertyService;

    @Scheduled(fixedRateString = "#{@propertyService.getWorkerInterval()}")
    // @Async
    public void worker() {
        if (!propertyService.isScheduleWorkerEnabled()) {
            return; // Schedule nicht aktiv
        }

        LocalTime now = LocalTime.now();
        Date heute = new Date();

        // Schon genug in dem Interval verarbeitet?
        Date abStart = new Date(heute.getTime() - propertyService.getScheduleWorkerInterval().toMillis());

        long verarbeitet = pushOrderRepository.countBySendGreaterThan(abStart);
        if (verarbeitet >= propertyService.getQuantity()) {
            return; // genug verarbeitet
        }

        List<PushOrder> orders =
                pushOrderRepository.findBySendIsNullOrderByExpiration(
                        PageRequest.of(0, 1)); // immer nur 1 Order pro Durchlauf

        boolean hasDeleted = false;
        for (PushOrder o : orders) {
            if (o.hasExpired()) { // PushOrder abgelaufen
                pushOrderRepository.delete(o); // Order löschen
                hasDeleted = true;
            } else {
                PushDevice pushDevice = pushDeviceRepository.findByUserid(o.getUserid());
                if (pushDevice == null) { // User hat kein PushDevice
                    pushOrderRepository.deletePushOrder(o.getUserid()); // alle unversendeten Order für User löschen
                    hasDeleted = true;
                } else {
                    if (!pushDevice.getKategorien().contains(o.getKategorie())) {
                        pushOrderRepository.deletePushOrder(o.getUserid(), o.getKategorie()); // alle unversendeten Order für User und Kategorie löschen
                        hasDeleted = true;
                    }
                }
            }
        }

        if (hasDeleted) {
            return; // neuen Durchlauf beginnen
        }

        if (now.isAfter(LocalTime.parse(propertyService.getStart()))
                && now.isBefore(LocalTime.parse(propertyService.getEnd()))
        ) {
            orders.forEach(
                    o -> {
                        int i = pushOrderRepository.updatePushOrder(o.getId(), heute); // setze Datum
                        if (i > 0) { // Update wurde gemacht
                            System.out.println("jetzt Pushen!");
                            // wenn Fehler beim Pushen: Order löschen, optional auch PushDevice löschen, wenn Token unregistered
                        }
                    });
        }
    }
}
