package com.example.booking.repository;

import com.example.booking.entity.Desk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeskRepository extends JpaRepository<Desk, Long> {
    Optional<Desk> findById(Integer id);
}
