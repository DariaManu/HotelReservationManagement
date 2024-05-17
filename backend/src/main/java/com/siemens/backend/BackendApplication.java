package com.siemens.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.backend.domain.model.Hotel;
import com.siemens.backend.domain.model.User;
import com.siemens.backend.domain.repository.HotelRepository;
import com.siemens.backend.domain.repository.RoomRepository;
import com.siemens.backend.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(HotelRepository hotelRepository,
                             RoomRepository roomRepository, UserRepository userRepository) {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Hotel>> typeReference = new TypeReference<List<Hotel>>() {};
            try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/hotels.json")) {
                List<Hotel> hotels = mapper.readValue(inputStream, typeReference);
                hotels.forEach(hotel -> {
                    roomRepository.saveAll(hotel.getRooms());
                });
                hotelRepository.saveAll(hotels);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            userRepository.save(new User("email", "password"));
        };
    }
}
