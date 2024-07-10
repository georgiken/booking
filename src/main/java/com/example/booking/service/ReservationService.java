package com.example.booking.service;


import com.example.booking.dto.ReservedRequest;
import com.example.booking.entity.Desk;
import com.example.booking.entity.Reservation;
import com.example.booking.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DeskService deskService;

    public List<Desk> getAvailableReservations(LocalDateTime startTime) {
        List<Reservation> reservations = reservationRepository.findByEndTimeBefore(startTime);
        if (reservations.isEmpty()) {
            return deskService.getAll();
        }
        List<Desk> desks = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Desk desk = reservation.getDesk();
            desk.setEndTime(desk.getEndTime().plusMinutes(1));
            desks.add(desk);
        }
        return desks;
    }

    public Optional<List<Reservation>> getUserReservations(ReservedRequest request) {
        return reservationRepository.findAllByUserId(request.getUserId());
    }

    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation deleteReservation(Reservation reservation) {
        return reservationRepository.deleteByDeskIdAndUserId(reservation.getDesk().getId(), reservation.getUser().getId());
    }

    public List<Reservation> getLastByUserIdAndDeskId(Integer userId, Integer deskId) {
       return reservationRepository.findAllByUserIdAndDeskId(userId, deskId);
    }
}
