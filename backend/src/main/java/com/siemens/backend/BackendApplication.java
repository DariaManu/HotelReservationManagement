package com.siemens.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.backend.domain.model.Hotel;
import com.siemens.backend.domain.model.Reservation;
import com.siemens.backend.domain.model.Room;
import com.siemens.backend.domain.model.User;
import com.siemens.backend.domain.repository.HotelRepository;
import com.siemens.backend.domain.repository.ReservationRepository;
import com.siemens.backend.domain.repository.RoomRepository;
import com.siemens.backend.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(HotelRepository hotelRepository,
                             RoomRepository roomRepository,
                             UserRepository userRepository,
                             ReservationRepository reservationRepository) {
        return args -> {
            User user = new User("email", "password");
            userRepository.save(user);
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Hotel>> typeReference = new TypeReference<List<Hotel>>() {};
            try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/hotels.json")) {
                List<Hotel> hotels = mapper.readValue(inputStream, typeReference);
                hotels.forEach(hotel -> {
                    roomRepository.saveAll(hotel.getRooms());
                });
                hotelRepository.saveAll(hotels);
                Hotel reservationHotel = hotels.get(0);
                Room reservationRoom = reservationHotel.getRooms().get(0);
                reservationRepository.save(new Reservation(user,
                        reservationHotel, reservationRoom,
                        LocalDate.of(2024, 4, 1),
                        LocalDate.of(2024, 4, 5)));
                reservationRepository.save(new Reservation(user,
                        reservationHotel, reservationRoom,
                        LocalDate.of(2024, 7, 1),
                        LocalDate.of(2024, 7, 5)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
