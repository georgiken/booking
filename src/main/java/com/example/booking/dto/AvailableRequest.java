package com.example.booking.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailableRequest {
    private LocalDate date;

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    private LocalTime time;
}
