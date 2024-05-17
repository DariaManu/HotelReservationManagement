package com.siemens.backend.api;

import com.siemens.backend.domain.model.Hotel;
import com.siemens.backend.domain.model.Reservation;
import com.siemens.backend.service.BusinessException;
import com.siemens.backend.service.HotelReservationManagementService;
import com.siemens.backend.service.ObjectNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/hotel")
@AllArgsConstructor
public class HotelReservationManagementController {
    private final HotelReservationManagementService service;

    @PostMapping
    public List<Hotel> getNearbyHotels(@RequestBody GetNearbyHotelsRequest request) {
        return service.getNearbyHotels(request.getRadius(), request.getUserLat(), request.getUserLon());
    }

    @PostMapping(path = "/reservation")
    public ResponseEntity<?> makeReservation(@RequestBody MakeReservationRequest request) {
        try {
            service.makeReservation(request.getUserId(), request.getHotelId(), request.getRoomId(),
                    request.getStartDate(), request.getEndDate());
        } catch (BusinessException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        } catch (ObjectNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/{hotelId}/reservation/user/{userId}")
    public ResponseEntity<?> getReservationsForHotelAndUser(@PathVariable final Long hotelId, @PathVariable final Long userId) {
        List<Reservation> reservations;
        try {
            reservations = service.getReservationsForHotelAndUser(hotelId, userId);
        } catch (ObjectNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .body(reservations.stream()
                        .map((reservation ->
                                new ReservationsForHotelAndUserDTO(reservation.getId(),
                                        reservation.getHotel().getName(),
                                        reservation.getRoom().getRoomNumber(),
                                        reservation.getRoom().getType().toString(),
                                        reservation.getStartDate(),
                                        reservation.getEndDate())))
                        .collect(Collectors.toList()));
    }

    @DeleteMapping(path = "/reservation/{reservationId}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable final Long reservationId) {
        try {
            service.cancelReservation(reservationId);
        } catch (ObjectNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (BusinessException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/reservation/{reservationId}/change/{newRoomId}")
    public ResponseEntity<?> changeReservation(@PathVariable final Long reservationId, @PathVariable final Long newRoomId) {
        try {
            service.changeRoomInReservation(reservationId, newRoomId);
        } catch (ObjectNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (BusinessException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/{hotelId}/feedback")
    public ResponseEntity<?> postUserFeedback(@PathVariable final Long hotelId, @RequestBody PostUserFeedbackRequest request) {
        try {
            service.postUserFeedback(hotelId, request.getFeedback());
        } catch (ObjectNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/{hotelId}/feedback")
    public ResponseEntity<?> getUserFeedbackForHotel(@PathVariable final Long hotelId) {
        List<String> feedback;
        try {
            feedback = service.getUserFeedbackForHotel(hotelId);
        } catch (ObjectNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().body(feedback);
    }
}
