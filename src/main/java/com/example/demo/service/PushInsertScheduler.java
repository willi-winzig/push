package com.example.demo.service;

import com.example.demo.entity.Kategorie;
import com.example.demo.entity.PushOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PushInsertScheduler {

    @Autowired private PushOrderService pushOrderService;

    @Value("${schedule.insert.enabled}")
    private boolean scheduleInsertEnabled;

    @Scheduled(fixedRateString = "#{@propertyService.getInsertInterval()}")
    public void insert() {
        if (scheduleInsertEnabled) {
            PushOrder pushOrder = new PushOrder(162032L, Kategorie.POSTFACH, "param", 100L);
            pushOrderService.save(pushOrder);
        }
    }
}
