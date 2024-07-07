package com.example.booking.dto;

import java.time.LocalDate;

public class AvailableRequest {
    private LocalDate date;
    private Time time;

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
