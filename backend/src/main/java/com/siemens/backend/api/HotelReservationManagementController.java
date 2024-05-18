package com.siemens.backend.api;

import com.siemens.backend.api.request.GetNearbyHotelsRequest;
import com.siemens.backend.api.request.MakeReservationRequest;
import com.siemens.backend.api.request.PostUserFeedbackRequest;
import com.siemens.backend.api.response.ReservationsForHotelAndUserDTO;
import com.siemens.backend.domain.model.Hotel;
import com.siemens.backend.domain.model.Reservation;
import com.siemens.backend.service.exception.BusinessException;
import com.siemens.backend.service.HotelReservationManagementService;
import com.siemens.backend.service.exception.ObjectNotFoundException;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for hotels, reservations and user feedback
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/hotel")
@AllArgsConstructor
public class HotelReservationManagementController {
    private final HotelReservationManagementService service;

    /**
     * Get all hotels in a radius relative to the user.
     * @param request - request containing the radius and the position of the user
     * @return list of hotels within the radius
     */
    @PostMapping
    public List<Hotel> getNearbyHotels(@RequestBody GetNearbyHotelsRequest request) {
        return service.getNearbyHotels(request.getRadius(), request.getUserLat(), request.getUserLon());
    }

    /**
     * Make a reservation for a room.
     * @param request - request containing the id of the user, id of the hotel, id of the room and the time period.
     * @return ResponseEntity.ok(), if the reservation was made successfully
     *         ResponseEntity.status(HttpStatus.BAD_REQUEST), if business logic issue occurred
     *         ResponseEntity.status(HttpStatus.NOT_FOUND), if one of the ids in the request does not correspond
     *              to an existing object
     */
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

    /**
     * Get all reservations made by a usr at a particular hotel.
     * @param hotelId - id of the hotel
     * @param userId - id of the user
     * @return ResponseEntity.status(HttpStatus.NOT_FOUND), if the hotel does not exist
     *         ResponseEntity.ok() containing the list of reservations, otherwise
     */
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

    /**
     * Cancel a reservation.
     * @param reservationId - id of the reservation
     * @return ResponseEntity.status(HttpStatus.NOT_FOUND), if the reservation does not exist
     *         ResponseEntity.status(HttpStatus.BAD_REQUEST), if the reservation could not be cancelled
     *         ResponseEntity.ok(), if the reservation was cancelled successfully
     */
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

    /**
     * Change the room in a reservation.
     * @param reservationId - id of the reservation
     * @param newRoomId - id of the new room
     * @return ResponseEntity.status(HttpStatus.NOT_FOUND), if the reservation or the new room do not exist
     *         ResponseEntity.status(HttpStatus.BAD_REQUEST), if the reservation cannot be modified
     *         ResponseEntity.ok(), if the room was changed successfully
     */
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

    /**
     * Add user feedback to a hotel.
     * @param hotelId - id of the hotel
     * @param request - request containing the feedback from the user
     * @return ResponseEntity.status(HttpStatus.NOT_FOUND), if the hotel does not exist
     *         ResponseEntity.ok(), if the feedback was added successfully
     */
    @PostMapping(path = "/{hotelId}/feedback")
    public ResponseEntity<?> postUserFeedback(@PathVariable final Long hotelId, @RequestBody PostUserFeedbackRequest request) {
        try {
            service.postUserFeedback(hotelId, request.getFeedback());
        } catch (ObjectNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Get all user feedback for a hotel.
     * @param hotelId - id of the hotel
     * @return ResponseEntity.status(HttpStatus.NOT_FOUND), if the hotel does not exist
     *         ResponseEntity.ok(), otherwise
     */
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
