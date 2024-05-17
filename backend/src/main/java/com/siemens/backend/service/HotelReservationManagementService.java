package com.siemens.backend.service;

import com.siemens.backend.domain.model.*;
import com.siemens.backend.domain.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HotelReservationManagementService {
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final UserFeedbackRepository userFeedbackRepository;

    private static final LocalTime checkInTime = LocalTime.of(12, 0, 0);

    public List<Hotel> getNearbyHotels(final int radius, final double latUser, final double lonUser) {
        List<Hotel> nearbyHotels = new ArrayList<>();
        hotelRepository.findAll().forEach(hotel -> {
            double distanceBetweenUserAndHotel = getDistanceBetweenPosition(latUser, lonUser, hotel.getLatitude(), hotel.getLongitude());
            if (Double.compare(distanceBetweenUserAndHotel, (double) radius) < 0) {
                nearbyHotels.add(hotel);
            }
        });
        return nearbyHotels;
    }

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

    public void makeReservation(final Long userId, final Long hotelId, final Long roomId,
                                final LocalDate startDate, final LocalDate endDate) {
        User user = userRepository.findById(userId).orElseThrow(ObjectNotFoundException::new);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(ObjectNotFoundException::new);
        Room room = roomRepository.findById(roomId).orElseThrow(ObjectNotFoundException::new);

        if(startDate.isAfter(endDate) || startDate.isEqual(endDate)) {
            throw new BusinessException("Start date needs to be before end date");
        }

        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today)) {
            throw new BusinessException("Start date needs to be after today");
        }

        boolean roomCanBeBooked = roomCanBeBooked(room, startDate, endDate);
        if (!roomCanBeBooked) {
            throw new BusinessException("Cannot book this room between " + startDate + " and " + endDate);
        }

        Reservation reservation = new Reservation(user, hotel, room, startDate, endDate);
        reservationRepository.save(reservation);
    }

    private boolean roomCanBeBooked(final Room room, final LocalDate startDate, final LocalDate endDate) {
        List<Reservation> reservations = reservationRepository.getAllByRoom(room);
        List<Reservation> overlappingReservations = new ArrayList<>();
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

    public List<Reservation> getReservationsForHotelAndUser(final Long hotelId, final Long userId) {
        User user = userRepository.findById(userId).orElseThrow(ObjectNotFoundException::new);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(ObjectNotFoundException::new);
        List<Reservation> reservations = reservationRepository.getAllByHotelAndUser(hotel, user);
        return reservations;
    }

    public void cancelReservation(final Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(ObjectNotFoundException::new);
        checkIfModifyingReservationIsAllowed(reservation);
        reservationRepository.delete(reservation);
    }

    public void changeRoomInReservation(final Long reservationId, final Long roomId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(ObjectNotFoundException::new);
        Room newRoom = roomRepository.findById(roomId).orElseThrow(ObjectNotFoundException::new);
        checkIfModifyingReservationIsAllowed(reservation);
        boolean roomCanBeBooked = roomCanBeBooked(newRoom, reservation.getStartDate(), reservation.getEndDate());
        if (!roomCanBeBooked) {
            throw new BusinessException("This room is already booked between that period");
        }
        if (!reservation.getHotel().getRooms().contains(newRoom)) {
            throw new BusinessException("The hotel at which this reservation was made does not have the specified room");
        }
        reservation.setRoom(newRoom);
        reservationRepository.save(reservation);
    }

    private void checkIfModifyingReservationIsAllowed(final Reservation reservation) {
        LocalDate currentDate = LocalDate.now();
        if (reservation.getStartDate().isBefore(currentDate)) {
            throw new BusinessException("Cannot modify this reservation");
        } else if (reservation.getStartDate().equals(currentDate)) {
            LocalTime time = LocalTime.now();
            if (checkInTime.minusHours(2).isBefore(time)) {
                throw new BusinessException("Cannot modify this reservation");
            }
        }
    }

    public void postUserFeedback(final Long hotelId, final String feedback) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(ObjectNotFoundException::new);
        UserFeedback userFeedback = new UserFeedback(hotel, feedback);
        userFeedbackRepository.save(userFeedback);
    }

    public List<String> getUserFeedbackForHotel(final Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(ObjectNotFoundException::new);
        return userFeedbackRepository.getAllByHotel(hotel).stream().map(UserFeedback::getFeedback).collect(Collectors.toList());
    }
}
