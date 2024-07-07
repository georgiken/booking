package com.example.booking.repository;

import com.example.booking.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByEndTimeBefore(LocalDateTime startTime);

    Optional<List<Reservation>> findAllByUserId(Integer userId);

    Reservation deleteByDeskIdAndUserId(Integer id, Integer id1);

    Reservation findAllByUserIdAndDeskId(Integer userId, Integer deskId);
}
