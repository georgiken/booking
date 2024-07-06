package com.example.booking.controller;

import com.example.booking.dto.AvailableRequest;
import com.example.booking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/available")
    public ResponseEntity<List<?>> getAvailableReservations(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        AvailableRequest request = new AvailableRequest();
        request.setDate(date);
        request.setTime(time);
        return ResponseEntity.ok(reservationService.getAvailableReservations(request));
    }
}
