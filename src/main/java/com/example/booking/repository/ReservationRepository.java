package com.example.booking.repository;

import com.example.booking.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByEndTimeAfter(LocalDateTime startTime);
}
