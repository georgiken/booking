package com.example.booking.controller;

import com.example.booking.dto.AvailableRequest;
import com.example.booking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/available")
    public ResponseEntity<List<?>> getAvailableReservations(AvailableRequest request) {
        return ResponseEntity.ok(reservationService.getAvailableReservations(request));
    }
}
