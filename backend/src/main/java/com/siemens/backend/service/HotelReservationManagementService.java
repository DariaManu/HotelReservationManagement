package com.siemens.backend.service;

import com.siemens.backend.domain.model.User;
import com.siemens.backend.domain.model.Hotel;
import com.siemens.backend.domain.model.Room;
import com.siemens.backend.domain.model.Reservation;
import com.siemens.backend.domain.model.UserFeedback;
import com.siemens.backend.domain.repository.UserRepository;
import com.siemens.backend.domain.repository.HotelRepository;
import com.siemens.backend.domain.repository.RoomRepository;
import com.siemens.backend.domain.repository.ReservationRepository;
import com.siemens.backend.domain.repository.UserFeedbackRepository;
import com.siemens.backend.service.exception.BusinessException;
import com.siemens.backend.service.exception.ObjectNotFoundException;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class containing the business logic for reservations and user feedback.
 */
@Service
@AllArgsConstructor
public class HotelReservationManagementService {
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final UserFeedbackRepository userFeedbackRepository;

    // Check-in time for reservations
    private static final LocalTime checkInTime = LocalTime.of(12, 0, 0);

    /**
     * Get all hotels which are within a radius relative to the position of the user.
     * @param radius - the radius the hotels must be in (the maximum distance between the position of the user
     *               and the position of a hotel)
     * @param latUser - latitude coordinated of a user
     * @param lonUser - longitude coordinates of a user
     * @return list of hotels which are in the specified radius
     */
    public List<Hotel> getNearbyHotels(final int radius, final double latUser, final double lonUser) {
        final List<Hotel> nearbyHotels = new ArrayList<>();
        hotelRepository.findAll().forEach(hotel -> {
            final double distanceBetweenUserAndHotel = getDistanceBetweenPosition(latUser, lonUser, hotel.getLatitude(), hotel.getLongitude());
            if (Double.compare(distanceBetweenUserAndHotel, (double) radius) < 0) {
                nearbyHotels.add(hotel);
            }
        });
        return nearbyHotels;
    }

    /**
     * Get the distance in kilometers between a user and a hotel.
     * Steps:
     *  1. Calculate the length of a degree of latitude and longitude in meters.
     *  2. Convert the latitude and longitude coordinates of the user and hotel to meters.
     *  3. Calculate the distance between the position of the user and the position of the hotel using the
     *  Euclidean distance formula. The result is in meters.
     *  4. Return the distance between the 2 points in kilometers.
     * Formulas taken from: <a href="https://en.wikipedia.org/wiki/Geographic_coordinate_system">Formulas</a>
     * @param latUser - latitude coordinate of the user
     * @param lonUser - longitude coordinate of the user
     * @param latHotel - latitude coordinate of the hotel
     * @param lonHotel - longitude coordinate of the hotel
     * @return the distance in kilometers between 2 points on Earth
     */
    private double getDistanceBetweenPosition(final double latUser, final double lonUser, final double latHotel, final double lonHotel) {
        final double averageLat = (latUser + latHotel) / 2.0;
        final double metersPerDegreeOfLatitude = 111132.954 - 559.822 * Math.cos( 2.0 * averageLat ) + 1.175 * Math.cos( 4.0 * averageLat);
        final double metersPerDegreeOfLongitude = 111412.84 * Math.cos(averageLat) - 93.5 * Math.cos(3.0 * averageLat) + 0.118 * Math.cos(5 * averageLat);

        final double latUserInMeters = latUser * metersPerDegreeOfLatitude;
        final double lonUserInMeters = lonUser * metersPerDegreeOfLongitude;

        final double latHotelInMeters = latHotel * metersPerDegreeOfLatitude;
        final double lonHotelInMeters = lonHotel * metersPerDegreeOfLongitude;

        final double distanceInMeters = Math.sqrt(Math.pow(latUserInMeters - latHotelInMeters, 2) + Math.pow(lonUserInMeters - lonHotelInMeters, 2));

        return distanceInMeters / 1000.0;
    }

    /**
     * Add a new reservation. Throws an exception if the reservation cannot be made.
     * @param userId - the id of the user making the reservation
     * @param hotelId - the id of the hotel the user wants to make a reservation at
     * @param roomId - the id of the room the user wants to reserve
     * @param startDate - the starting date of the reservation
     * @param endDate -the ending date of the reservation
     * @throws BusinessException if the starting date is after the ending date
     * @throws BusinessException if the starting date is before today
     * @throws BusinessException if there are reservations for the specified room which
     * overlap the proposed starting and ending dates
     */
    public void makeReservation(final Long userId, final Long hotelId, final Long roomId,
                                final LocalDate startDate, final LocalDate endDate) {
        final User user = userRepository.findById(userId).orElseThrow(ObjectNotFoundException::new);
        final Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(ObjectNotFoundException::new);
        final Room room = roomRepository.findById(roomId).orElseThrow(ObjectNotFoundException::new);

        if(startDate.isAfter(endDate) || startDate.isEqual(endDate)) {
            throw new BusinessException("Start date needs to be before end date");
        }

        final LocalDate today = LocalDate.now();
        if (startDate.isBefore(today)) {
            throw new BusinessException("Start date needs to be after today");
        }

        final boolean roomCanBeBooked = roomCanBeBooked(room, startDate, endDate);
        if (!roomCanBeBooked) {
            throw new BusinessException("Cannot book this room between " + startDate + " and " + endDate);
        }

        final Reservation reservation = new Reservation(user, hotel, room, startDate, endDate);
        reservationRepository.save(reservation);
    }

    /**
     * Check if a room can be booked between the proposed starting and ending dates.
     * @param room - the room a user wants to reserve
     * @param startDate - the starting date of the reservation
     * @param endDate - the ending date of the reservation
     * @return true, if there are no other reservations for the given room which overlap the
     * reservation period requested by the user
     *         false, otherwise
     */
    private boolean roomCanBeBooked(final Room room, final LocalDate startDate, final LocalDate endDate) {
        final List<Reservation> reservations = reservationRepository.getAllByRoom(room);
        final List<Reservation> overlappingReservations = new ArrayList<>();

        reservations.forEach((reservation -> {
            if (startDate.isBefore(reservation.getStartDate()) && endDate.isAfter(reservation.getEndDate())) {
                overlappingReservations.add(reservation);
            } else if (startDate.isEqual(reservation.getStartDate())
                    || (startDate.isAfter(reservation.getStartDate()) && startDate.isBefore(reservation.getEndDate()))) {
                overlappingReservations.add(reservation);
            } else if ((endDate.isAfter(reservation.getStartDate()) && endDate.isBefore(reservation.getEndDate())
                    ) || endDate.isEqual(reservation.getEndDate())) {
                overlappingReservations.add(reservation);
            }
        }));

        return overlappingReservations.isEmpty();
    }

    /**
     * Get all reservations made by a user at a particular hotel.
     * @param hotelId - id of the hotel
     * @param userId - id of the user
     * @return list of reservations made by the specified user at the specified hotel
     */
    public List<Reservation> getReservationsForHotelAndUser(final Long hotelId, final Long userId) {
        final User user = userRepository.findById(userId).orElseThrow(ObjectNotFoundException::new);
        final Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(ObjectNotFoundException::new);
        return reservationRepository.getAllByHotelAndUser(hotel, user);
    }

    /**
     * Cancel a reservation. If the reservation cannot be cancelled an exception is thrown.
     * @param reservationId - id of the reservation to be cancelled
     */
    public void cancelReservation(final Long reservationId) {
        final Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(ObjectNotFoundException::new);
        checkIfModifyingReservationIsAllowed(reservation);
        reservationRepository.delete(reservation);
    }

    /**
     * Change the room in a reservation. If the reservation cannot be modified and exception is thrown
     * @param reservationId - id of the reservation to be modified
     * @param roomId - id of the new room to be added in the reservation
     * @throws BusinessException if the new room cannot be booked during the interval of dates of the reservation
     * @throws BusinessException if the new room does not belong to the hotel at which the reservation was made
     */
    public void changeRoomInReservation(final Long reservationId, final Long roomId) {
        final Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(ObjectNotFoundException::new);
        final Room newRoom = roomRepository.findById(roomId).orElseThrow(ObjectNotFoundException::new);

        checkIfModifyingReservationIsAllowed(reservation);

        final boolean roomCanBeBooked = roomCanBeBooked(newRoom, reservation.getStartDate(), reservation.getEndDate());
        if (!roomCanBeBooked) {
            throw new BusinessException("This room is already booked between that period");
        }

        if (!reservation.getHotel().getRooms().contains(newRoom)) {
            throw new BusinessException("The hotel at which this reservation was made does not have the specified room");
        }

        reservation.setRoom(newRoom);
        reservationRepository.save(reservation);
    }

    /**
     * Check if a reservation can be modified (cancel or change a room)
     * Rules:
     *  - the starting date of the reservation must be after today's date
     *  - if the starting date of the reservation is today, then the time of the request
     *  cannot be later than two hours before the check-in time
     * @param reservation - reservation to be modified
     * @throws BusinessException if the rules are not respected
     */
    private void checkIfModifyingReservationIsAllowed(final Reservation reservation) {
        final LocalDate currentDate = LocalDate.now();
        if (reservation.getStartDate().isBefore(currentDate)) {
            throw new BusinessException("Cannot modify this reservation");
        } else if (reservation.getStartDate().equals(currentDate)) {
            final LocalTime time = LocalTime.now();
            if (checkInTime.minusHours(2).isBefore(time)) {
                throw new BusinessException("Cannot modify this reservation");
            }
        }
    }

    /**
     * Add a user feedback to a hotel.
     * @param hotelId - id of the hotel
     * @param feedback - feedback from the user about the hotel
     */
    public void postUserFeedback(final Long hotelId, final String feedback) {
        final Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(ObjectNotFoundException::new);
        final UserFeedback userFeedback = new UserFeedback(hotel, feedback);
        userFeedbackRepository.save(userFeedback);
    }

    /**
     * Get all user feedback given to a particular hotel
     * @param hotelId - id of the hotel
     * @return list of feedback from the users about the specified hotel
     */
    public List<String> getUserFeedbackForHotel(final Long hotelId) {
        final Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(ObjectNotFoundException::new);
        return userFeedbackRepository.getAllByHotel(hotel).stream().map(UserFeedback::getFeedback).collect(Collectors.toList());
    }
}
