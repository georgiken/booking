package com.example.booking.service;


import com.example.booking.dto.AvailableRequest;
import com.example.booking.entity.Reservation;
import com.example.booking.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DeskService deskService;

    public List<?> getAvailableReservations(AvailableRequest request) {
        LocalDateTime startTime = LocalDateTime.of(request.getDate(), request.getTime());
        List<Reservation> reservations = reservationRepository.findByEndTimeAfter(startTime);
        if (reservations.isEmpty()) {
            return deskService.getAll();
        }
        return reservations;
    }

}
