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

    @Value("${schedule.worker.enabled}")
    private boolean scheduleWorkerEnabled;

    @Value("${schedule.worker.start}")
    private String start;

    @Value("${schedule.worker.end}")
    private String end;

    public int getQuantity() {
        return quantity;
    }

    public Duration getScheduleInsertInterval() {
        return scheduleInsertInterval;
    }

    public Duration getScheduleWorkerInterval() {
        return scheduleWorkerInterval;
    }

    public boolean isScheduleWorkerEnabled() {
        return scheduleWorkerEnabled;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

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
