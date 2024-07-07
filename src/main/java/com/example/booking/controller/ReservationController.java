package com.example.booking.controller;

import com.example.booking.dto.AvailableRequest;
import com.example.booking.dto.ReservationRequest;
import com.example.booking.dto.ReservedRequest;
import com.example.booking.entity.Desk;
import com.example.booking.entity.Reservation;
import com.example.booking.entity.User;
import com.example.booking.service.DeskService;
import com.example.booking.service.ReservationService;
import com.example.booking.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private DeskService deskService;
    @Autowired
    private UserService userService;

    @GetMapping("/available")
    public ResponseEntity<List<Desk>> getAvailableReservations(@RequestBody AvailableRequest request) {

        LocalDateTime formatted_request = LocalDateTime.of(request.getDate(),
                LocalTime.of(request.getHours(), request.getMinutes()));

        List<Desk> availables = reservationService.getAvailableReservations(formatted_request);
        if (availables.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(availables);
    }

    @GetMapping("/")
    public ResponseEntity<Optional<List<Reservation>>> getUserReservation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = ((User) authentication.getPrincipal()).getEmail();
        log.info("email =" + email);
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Integer userId = user.get().getId();

        ReservedRequest request = new ReservedRequest();
        request.setUserId(userId);
        Optional<List<Reservation>> reservations = reservationService.getUserReservations(request);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/")
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest request) {
        Reservation reservation = new Reservation();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId =(Integer) authentication.getPrincipal();
        Optional<User> user = userService.getUserById(userId);
        if (user.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        reservation.setUser(user.get());

        LocalDateTime startTime = LocalDateTime.of(request.getDate(),
                LocalTime.of(request.getHours(), request.getMinutes()));
        reservation.setStartTime(startTime);
        reservation.setEndTime(startTime.plusHours(2));
        if (startTime.getHour()<12 || startTime.getHour()>22){
            return ResponseEntity.badRequest().body(reservation);
        }

        Optional<Desk> desk = deskService.getById(request.getDeskId());
        if (desk.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        else if (desk.get().getEndTime().isBefore(startTime)){
            return ResponseEntity.badRequest().body(reservation);
        }

        Desk newDesk = desk.get();
        newDesk.setEndTime(reservation.getEndTime());
        reservation.setDesk(deskService.update(newDesk));
        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    @PutMapping("/update")
    public ResponseEntity<Reservation> updateReservation(@RequestBody ReservationRequest request) {
        Reservation reservation = new Reservation();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId =(Integer) authentication.getPrincipal();
        Optional<User> user = userService.getUserById(userId);
        if (user.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        reservation.setUser(user.get());

        LocalDateTime startTime = LocalDateTime.of(request.getDate(),
                LocalTime.of(request.getHours(), request.getMinutes()));
        reservation.setStartTime(startTime);
        reservation.setEndTime(startTime.plusHours(2));

        Optional<Desk> desk = deskService.getById(request.getDeskId());
        if (desk.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Desk newDesk = desk.get();
        newDesk.setEndTime(reservation.getEndTime());
        reservation.setDesk(deskService.update(newDesk));
        return ResponseEntity.ok(reservationService.updateReservation(reservation));


    }


    @DeleteMapping("/")
    public ResponseEntity<Reservation> deleteReservation(@RequestBody Integer deskId) {
        LocalDateTime now = LocalDateTime.now();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId =(Integer) authentication.getPrincipal();
        Optional<User> user = userService.getUserById(userId);
        if (user.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        Optional<Desk> desk = deskService.getById(deskId);
        if (desk.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        Reservation reservation = reservationService.getByUserIdAndDeskId(userId, deskId);
        if (reservation == null){
            return ResponseEntity.noContent().build();
        }
        else if (reservation.getStartTime().isBefore(now.plusHours(1))){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reservationService.deleteReservation(reservation));
    }
}
