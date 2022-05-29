package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ConverterService {

    @Value("${schedule.worker.quantity}")
    private int quantity;

    @Value("${schedule.insert.interval}")
    private Duration scheduleInsertInterval;

    @Value("${schedule.worker.interval}")
    private Duration scheduleWorkerInterval;

    public String getInsertInterval() {
        return String.valueOf(scheduleInsertInterval.toMillis());
    }

    /**
     * technisches Schedule-Intervall.
     *
     * @return Intervall in Millisekunden
     */
    public String getWorkerInterval() {

        long f = scheduleWorkerInterval.toMillis() / quantity;
        if (f <= 0) {
            f = 1;
        }
        return String.valueOf(f);
    }
}
