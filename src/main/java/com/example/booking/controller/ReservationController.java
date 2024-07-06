package com.example.booking.controller;

import com.example.booking.dto.AvailableRequest;
import com.example.booking.dto.ReservedRequest;
import com.example.booking.entity.Reservation;
import com.example.booking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/available")
    public ResponseEntity<List<?>> getAvailableReservations(@RequestBody AvailableRequest request) {
        List<?> availables = reservationService.getAvailableReservations(request);
        if (availables.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(availables);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Optional<List<Reservation>>> getReservation(@PathVariable Integer userId) {
        ReservedRequest request = new ReservedRequest();
        request.setUserId(userId);
        Optional<List<Reservation>> reservations = reservationService.getUserReservations(request);
        return ResponseEntity.ok(reservations);
    }


}
