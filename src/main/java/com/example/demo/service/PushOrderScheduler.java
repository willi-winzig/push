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

        // Schon genug in dem Interval verarbeitet?
        Date startDate = new Date(new Date().getTime() - propertyService.getScheduleWorkerInterval().toMillis());

        long verarbeitet = pushOrderRepository.countBySendGreaterThan(startDate);
        if (verarbeitet >= propertyService.getQuantity()) {
            return; // genug verarbeitet
        }

        List<PushOrder> pushOrders =
                pushOrderRepository.findBySendIsNullOrderByExpiration(
                        PageRequest.of(0, 1)); // immer nur 1 Order pro Durchlauf

        if (checkOrdersToDelete(pushOrders)) {
            return; // neuen Durchlauf beginnen, weil Orders gelöscht wurden
        }

        LocalTime now = LocalTime.now();
        if (now.isAfter(LocalTime.parse(propertyService.getStart()))
                && now.isBefore(LocalTime.parse(propertyService.getEnd()))
        ) {
            pushOrders.forEach(
                    pushOrder -> {
                        int countUpdates = pushOrderRepository.updatePushOrder(pushOrder.getId(), new Date()); // setze Versand-Datum
                        if (countUpdates > 0) { // Update wurde gemacht
                            System.out.println("jetzt Pushen!");
                            // wenn Fehler beim Pushen: Order löschen, optional auch PushDevice löschen, wenn Token unregistered
                        }
                    });
        }
    }

    private boolean checkOrdersToDelete(List<PushOrder> pushOrders) {
        for (PushOrder o : pushOrders) {
            if (o.hasExpired()) { // PushOrder abgelaufen
                pushOrderRepository.delete(o); // Order löschen
                return true;
            } else {
                PushDevice pushDevice = pushDeviceRepository.findPushDevice(o.getUserid());
                if (pushDevice == null) { // User hat kein PushDevice
                    pushOrderRepository.deletePushOrder(o.getUserid()); // alle unversendeten Order für User löschen
                    return true;
                } else {
                    if (!pushDevice.getKategorien().contains(o.getKategorie())) {
                        pushOrderRepository.deletePushOrder(o.getUserid(), o.getKategorie()); // alle unversendeten Order für User und Kategorie löschen
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
