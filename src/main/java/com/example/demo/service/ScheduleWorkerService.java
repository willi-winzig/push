package com.example.demo.service;

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

@Service
@EnableAsync
public class ScheduleWorkerService {

    @Autowired
    private PushOrderService pushOrderService;

    @Autowired
    private ConverterService converterService;

    @Scheduled(fixedRateString = "#{@converterService.getWorkerInterval()}")
    @Async
    public void worker() {
        if (!converterService.isScheduleWorkerEnabled()) {
            return; // Schedule nicht aktiv
        }

        LocalTime now = LocalTime.now();
        Date heute = new Date();

        // Schon genug in dem Interval verarbeitet?
        Date abStart = new Date(heute.getTime() - converterService.getScheduleWorkerInterval().toMillis());

        long verarbeitet = pushOrderService.countBySendGreaterThan(abStart);
        if (verarbeitet >= converterService.getQuantity()) {
            return; // genug verarbeitet
        }

        List<PushOrder> orders =
                pushOrderService.findBySendIsNullOrderByOrdertsp(
                        PageRequest.of(0, 1)); // immer nur 1 Order pro Durchlauf

        if (now.isAfter(LocalTime.parse(converterService.getStart()))
                && now.isBefore(LocalTime.parse(converterService.getEnd()))
                && !orders.isEmpty()) {
            orders.forEach(
                    o -> {
                        int i = pushOrderService.updatePushOrder(o.getId(), heute); // setze Datum
                        if (i > 0) { // Update wurde gemacht
                            System.out.println("jetzt Pushen!");
                        }
                    });
        }
    }
}
