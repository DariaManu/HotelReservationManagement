package com.siemens.backend.service;

import com.siemens.backend.domain.model.Hotel;
import com.siemens.backend.service.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HotelReservationManagementServiceTest {
    @Autowired
    private HotelReservationManagementService service;

    @Test
    public void getNearbyHotelsReturnsHotelsInSpecifiedRadius() {
        final double currentLat = 46.75382429219862;
        final double currentLon = 23.546874280441084;

        int radius1 = 10;
        List<Hotel> hotelsInRadius1 = service.getNearbyHotels(radius1, currentLat, currentLon);
        assertEquals(3, hotelsInRadius1.size());

        int radius2 = 6;
        List<Hotel> hotelsInRadius2 = service.getNearbyHotels(radius2, currentLat, currentLon);
        assertEquals(1, hotelsInRadius2.size() );
        assertEquals(1, hotelsInRadius2.get(0).getId());

        int radius3 = 1;
        List<Hotel> hotelsInRadius3 = service.getNearbyHotels(radius3, currentLat, currentLon);
        assertEquals(0, hotelsInRadius3.size());
    }

    @Test
    public void makingReservationThrowsException() {
        BusinessException exception1 = assertThrows(BusinessException.class,
                () -> service.makeReservation(1L, 1L, 1L,
                LocalDate.of(2024, 6, 12),
                LocalDate.of(2024, 6, 9)));
        assertEquals("Start date needs to be before end date", exception1.getMessage());

        BusinessException exception2 = assertThrows(BusinessException.class,
                () -> service.makeReservation(1L, 1L, 1L,
                        LocalDate.now().minusDays(5), LocalDate.now().minusDays(3)));
        assertEquals("Start date needs to be after today", exception2.getMessage());

        BusinessException exception3 = assertThrows(BusinessException.class,
                () -> service.makeReservation(1L, 1L, 1L,
                        LocalDate.of(2024, 7, 1),
                        LocalDate.of(2024, 7, 5)));
        assertEquals("Cannot book this room between 2024-07-01 and 2024-07-05", exception3.getMessage());

        assertThrows(BusinessException.class,
                () -> service.makeReservation(1L, 1L, 1L,
                        LocalDate.of(2024, 6, 29),
                        LocalDate.of(2024, 7, 7)));

        assertThrows(BusinessException.class,
                () -> service.makeReservation(1L, 1L, 1L,
                        LocalDate.of(2024, 7, 2),
                        LocalDate.of(2024, 7, 7)));

        assertThrows(BusinessException.class,
                () -> service.makeReservation(1L, 1L, 1L,
                        LocalDate.of(2024, 6, 29),
                        LocalDate.of(2024, 7, 5)));
    }

    @Test
    public void cancelingReservationThrowsException() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.cancelReservation(1L));
        assertEquals("Cannot modify this reservation", exception.getMessage());
    }

    @Test
    public void changingRoomInReservationThrowsException() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.changeRoomInReservation(2L, 4L));
        assertEquals("The hotel at which this reservation was made does not have the specified room", exception.getMessage());
    }
}