package com.example.booking.controller;

import com.example.booking.dto.*;
import com.example.booking.entity.Desk;
import com.example.booking.entity.Reservation;
import com.example.booking.entity.User;
import com.example.booking.service.DeskService;
import com.example.booking.service.ReservationService;
import com.example.booking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    @Operation(summary = "Получение самых ранних доступных столиков")
    public ResponseEntity<?> getAvailableReservations(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                               @RequestParam("time.hour") int hour,
                                               @RequestParam("time.minute") int minute) {
        LocalDateTime formattedRequest = LocalDateTime.of(date, LocalTime.of(hour, minute));

        List<Desk> availables = reservationService.getAvailableReservations(formattedRequest);
        if (availables.isEmpty()){
            return ResponseEntity.ok(new MessageResponse("Нет доступных столиков"));
        }
        return ResponseEntity.ok(availables);
    }

    @PostMapping("/")
    @Operation(summary = "Создание новой брони")
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
        Reservation reservation = new Reservation();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((User) authentication.getPrincipal()).getEmail();
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isEmpty()){
            return ResponseEntity.badRequest().body(new ErrorResponse(400, "нет такого пользователя",
                    "не найден"));
        }
        reservation.setUser(user.get());

        LocalDateTime startTime = LocalDateTime.of(request.getDate(),
                LocalTime.of(request.getTime().getHour(), request.getTime().getMinute()));
        reservation.setStartTime(startTime);
        reservation.setEndTime(startTime.plusHours(2));
        if (startTime.getHour()<12 || startTime.getHour()>22){
            return ResponseEntity.badRequest().body(new ErrorResponse(400, "некорректное время",
                    "бронь доступна после 12 и до 22"));
        }

        Optional<Desk> desk = deskService.getById(request.getDeskId());
        if (desk.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(400, "некорректный id стола",
                            "такого id не существует")
            );
        }
        else if (desk.get().getEndTime().isAfter(startTime)){
            return ResponseEntity.badRequest().body(new ErrorResponse(400,
                    "в это время стол уже забронирован",
                    "посмотрите другое время брони"));
        }

        Desk newDesk = desk.get();
        newDesk.setEndTime(reservation.getEndTime());
        reservation.setDesk(deskService.update(newDesk));
        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    @GetMapping("/")
    @Operation(summary = "Получение бронирований пользователя")
    public ResponseEntity<?> getUserReservation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = ((User) authentication.getPrincipal()).getEmail();
        log.info("email =" + email);
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Пользователь не найден", "Повторите авторизацию"));
        }
        Integer userId = user.get().getId();

        ReservedRequest request = new ReservedRequest();
        request.setUserId(userId);
        Optional<List<Reservation>> reservations = reservationService.getUserReservations(request);
        return reservations.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/update")
    @Operation(summary = "Обновление существующей брони по id стола")
    public ResponseEntity<?> updateReservation(@RequestBody ReservationRequest request) {
        Reservation reservation = new Reservation();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((User) authentication.getPrincipal()).getEmail();
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isEmpty()){
            ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Пользователь не найден", "Повторите авторизацию"));
        }
        reservation.setUser(user.get());

        LocalDateTime startTime = LocalDateTime.of(request.getDate(),
                LocalTime.of(request.getTime().getHour(), request.getTime().getMinute()));
        reservation.setStartTime(startTime);
        reservation.setEndTime(startTime.plusHours(2));

        Optional<Desk> desk = deskService.getById(request.getDeskId());
        if (desk.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Такого столика нет", "Проверьте доступные столики"));
        }

        Desk newDesk = desk.get();
        newDesk.setEndTime(reservation.getEndTime());
        reservation.setDesk(deskService.update(newDesk));
        return ResponseEntity.ok(reservationService.updateReservation(reservation));


    }


    @DeleteMapping("/")
    @Operation(summary = "Удаление брони пользователя по id стола")
    public ResponseEntity<?> deleteReservation(@RequestBody Integer deskId) {
        LocalDateTime now = LocalDateTime.now();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((User) authentication.getPrincipal()).getEmail();
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isEmpty()){
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    400, "пользователь не найден", "Повторите авторизацию"
            ));
        }

        Optional<Desk> desk = deskService.getById(deskId);
        if (desk.isEmpty()){
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Такого столика нет", "Проверьте доступные столики"));
        }
        Integer userId = user.get().getId();
        List<Reservation> reservations = reservationService.getLastByUserIdAndDeskId(userId, deskId);
        if (reservations.isEmpty()){
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    400, "такой брони нет",
                    "проверьте id стола"
            ));
        }
        Reservation lastReservation = reservations.get(reservations.size()-1);
        if (lastReservation.getStartTime().isBefore(now.plusHours(1))){
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    400, "слишком поздно для отмены брони",
                    "придётся прийти")
            );
        }

        return ResponseEntity.ok(reservationService.deleteReservation(lastReservation));
    }
}
