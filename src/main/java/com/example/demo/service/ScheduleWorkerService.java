package com.example.demo.service;

import com.example.demo.entity.PushOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
@EnableAsync
public class ScheduleWorkerService {

    @Autowired private PushOrderService pushOrderService;

    @Value("${schedule.worker.enabled}")
    private boolean scheduleWorkerEnabled;

    @Value("${schedule.worker.quantity}")
    private int quantity;

    @Value("${schedule.worker.start}")
    private String start;

    @Value("${schedule.worker.end}")
    private String end;

    @Value("${schedule.worker.interval}")
    private Duration scheduleWorkerInterval;

    @Scheduled(fixedRateString = "#{@converterService.getWorkerInterval()}")
    @Async
    public void worker() {
        if (!scheduleWorkerEnabled) {
            return; // Schedule nicht aktiv
        }

        LocalTime now = LocalTime.now();
        Date heute = new Date();

        // Schon genug in dem Interval verarbeitet?
        Date abStart = new Date(heute.getTime() - scheduleWorkerInterval.toMillis());

        long verarbeitet = pushOrderService.countBySendGreaterThan(abStart);
        if (verarbeitet >= quantity) {
            return; // genug verarbeitet
        }

        List<PushOrder> orders =
                pushOrderService.findBySendIsNullOrderByOrdertsp(
                        PageRequest.of(0, 1)); // immer nur 1 Order pro Durchlauf

        if (now.isAfter(LocalTime.parse(start))
                && now.isBefore(LocalTime.parse(end))
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
