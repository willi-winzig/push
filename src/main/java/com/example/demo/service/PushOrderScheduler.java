package com.example.demo.service;

import com.example.demo.entity.PushDevice;
import com.example.demo.entity.PushOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@EnableAsync
public class PushOrderScheduler {

    @Autowired
    private PushOrderRepository pushOrderRepository;

    @Autowired
    private PushDeviceRepository pushDeviceRepository;

    @Autowired
    private PropertyService propertyService;

    @Scheduled(fixedRateString = "#{@propertyService.getWorkerInterval()}", timeUnit = TimeUnit.MILLISECONDS)
    @Async
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

        if (pushOrders.isEmpty()) {
            return; // nichts mehr gefunden
        }

        if (pushOrders.size() > 1) {
            throw new IllegalStateException();
        }
        PushOrder pushOrder = pushOrders.get(0);

        if (checkOrderToDelete(pushOrder)) {
            return; // neuen Durchlauf beginnen, weil Order gelöscht wurden
        }

        LocalTime now = LocalTime.now();
        if (now.isAfter(LocalTime.parse(propertyService.getStart()))
                && now.isBefore(LocalTime.parse(propertyService.getEnd()))
        ) {
            int countUpdates = pushOrderRepository.updatePushOrder(pushOrder.getId(), new Date()); // setze Versand-Datum
            if (countUpdates > 0) { // Update wurde gemacht
                System.out.println("push");
                // wenn Fehler beim Pushen: Order löschen, optional auch PushDevice löschen, wenn Token unregistered
            } else {
                System.out.println("no push");
            }
        }
    }

    private boolean checkOrderToDelete(PushOrder o) {

        boolean orderDeleted = false;

        if (o.hasExpired()) { // PushOrder abgelaufen
            pushOrderRepository.delete(o); // Order löschen
            orderDeleted = true;
        }
        long anz;
        PushDevice pushDevice = pushDeviceRepository.findPushDevice(o.getUserid());
        if (pushDevice == null) { // User hat kein PushDevice
            anz = pushOrderRepository.deletePushOrder(o.getUserid()); // alle unversendeten Order für User löschen
            orderDeleted = anz > 0;
        } else {
            if (!pushDevice.getKategorien().contains(o.getKategorie())) {
                anz = pushOrderRepository.deletePushOrder(o.getUserid(), o.getKategorie()); // alle unversendeten Order für User und Kategorie löschen
                orderDeleted = anz > 0;
            }
        }
        return orderDeleted;
    }
}
