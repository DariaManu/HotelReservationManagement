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
        List<Reservation> overlappingReservations = reservationRepository.getOverlappingReservations(roomId, startDate, endDate);
        if (!overlappingReservations.isEmpty()) {
            throw new BusinessException("Cannot book this room between " + startDate + " and " + endDate);
        }
        User user = userRepository.findById(userId).orElseThrow(ObjectNotFoundException::new);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(ObjectNotFoundException::new);
        Room room = roomRepository.findById(roomId).orElseThrow(ObjectNotFoundException::new);
        Reservation reservation = new Reservation(user, hotel, room, startDate, endDate);
        reservationRepository.save(reservation);
    }

    public void cancelReservation(final Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(ObjectNotFoundException::new);
        LocalDate currentDate = LocalDate.now();
        if (reservation.getStartDate().isBefore(currentDate)) {
            throw new BusinessException("Cannot cancel this reservation");
        } else if (reservation.getStartDate().equals(currentDate)) {
            LocalTime time = LocalTime.now();
            LocalTime checkInTime = LocalTime.of(12, 0, 0);
            if (checkInTime.minusHours(2).isBefore(time)) {
                throw new BusinessException("Cannot cancel this reservation");
            }
        }
        reservationRepository.delete(reservation);
    }

    public void changeRoomInReservation(final Long reservationId, final Long roomId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(ObjectNotFoundException::new);
        LocalDate date = LocalDate.now();
        if (reservation.getStartDate().isBefore(date)) {
            throw new BusinessException("Cannot cancel this reservation");
        } else if (reservation.getStartDate().equals(date)) {
            LocalTime time = LocalTime.now();
            LocalTime checkInTime = LocalTime.of(12, 0, 0);
            if (checkInTime.minusHours(2).isBefore(time)) {
                throw new BusinessException("Cannot cancel this reservation");
            }
        }
        Room newRoom = roomRepository.findById(roomId).orElseThrow(ObjectNotFoundException::new);
        if (!reservation.getHotel().getRooms().contains(newRoom)) {
            throw new BusinessException("The hotel at which this reservation was made does not have the specified room");
        }
        reservation.setRoom(newRoom);
        reservationRepository.save(reservation);
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
