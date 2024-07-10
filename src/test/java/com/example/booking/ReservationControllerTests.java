package com.example.booking;

import com.example.booking.controller.ReservationController;
import com.example.booking.dto.AvailableRequest;
import com.example.booking.dto.ReservationRequest;
import com.example.booking.entity.Desk;
import com.example.booking.entity.Reservation;
import com.example.booking.entity.User;
import com.example.booking.service.DeskService;
import com.example.booking.service.ReservationService;
import com.example.booking.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ReservationController.class)
public class ReservationControllerTests {

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeskService deskService;

    @Test
    public void testGetAvailableReservations() throws Exception {
        AvailableRequest request = new AvailableRequest();
        request.setDate(LocalDate.now());
        request.setHours(10);
        request.setMinutes(0);

        List<Desk> availableDesks = new ArrayList<>();
        availableDesks.add(new Desk());

        when(reservationService.getAvailableReservations(any(LocalDateTime.class)))
                .thenReturn(availableDesks);


        mockMvc.perform(get("/api/reservations/available")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testCreateReservation() throws Exception {

        ReservationRequest request = new ReservationRequest();
        request.setDate(LocalDate.now());
        request.setHours(14);
        request.setMinutes(0);
        request.setDeskId(1);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(1);

        User mockUser = new User();
        mockUser.setId(1);
        Desk mockDesk = new Desk();
        mockDesk.setId(1);

        when(userService.getUserById(anyInt())).thenReturn(Optional.of(mockUser));
        when(deskService.getById(anyInt())).thenReturn(Optional.of(mockDesk));
        when(deskService.update(any(Desk.class))).thenReturn(mockDesk);

        Reservation mockReservation = new Reservation();
        mockReservation.setId(1);
        when(reservationService.createReservation(any(Reservation.class))).thenReturn(mockReservation);

        mockMvc.perform(post("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }

    @Test
    public void testDeleteReservation() throws Exception {
        Integer deskId = 1;

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(1); // Assuming userId

        User mockUser = new User();
        mockUser.setId(1);
        Desk mockDesk = new Desk();
        mockDesk.setId(1);
        Reservation mockReservation = new Reservation();
        mockReservation.setId(1);

        when(userService.getUserById(anyInt())).thenReturn(Optional.of(mockUser));
        when(deskService.getById(anyInt())).thenReturn(Optional.of(mockDesk));
        when(reservationService.getByUserIdAndDeskId(anyInt(), anyInt())).thenReturn(mockReservation);
        when(reservationService.deleteReservation(any(Reservation.class))).thenReturn(mockReservation);

        mockMvc.perform(delete("/api/reservations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deskId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }

}
