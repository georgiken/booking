package com.example.booking.service;


import com.example.booking.dto.AvailableRequest;
import com.example.booking.dto.ReservedRequest;
import com.example.booking.entity.Reservation;
import com.example.booking.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DeskService deskService;

    public List<?> getAvailableReservations(AvailableRequest request) {
        LocalDateTime startTime = LocalDateTime.of(request.getDate(), request.getTime());
        List<Reservation> reservations = reservationRepository.findByEndTimeBefore(startTime);
        if (reservations.isEmpty()) {
            return deskService.getAll();
        }
        return reservations;
    }

    public Optional<List<Reservation>> getUserReservations(ReservedRequest request) {
        return reservationRepository.findAllByUserId(request.getUserId());
    }
}
