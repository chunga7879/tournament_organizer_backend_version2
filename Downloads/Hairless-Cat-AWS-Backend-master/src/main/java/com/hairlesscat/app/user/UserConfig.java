package com.hairlesscat.app.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserConfig {
    @Bean(name = "user_init")
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return args -> {
            User user1 = User.builder()
                    .userId("111-222-333")
                    .fName("John")
                    .lName("Smith")
                    .department("Marketing")
                    .company("Amazon")
                    .email("johnsmith@gmail.com")
                    .pNumber("123456789")
                    .isAdmin(false)
					.tournaments(List.of())
                    .build();

            User user2 = User.builder()
                    .userId("222-333-444")
                    .fName("Alice")
                    .lName("Wong")
                    .department("Tech")
                    .company("Amazon")
                    .email("alicewong@gmail.com")
                    .pNumber("987654321")
                    .isAdmin(false)
					.tournaments(List.of())
                    .build();

			User user3 = User.builder()
				.userId("333-444-555")
				.fName("Kit")
				.lName("Kat")
				.department("Tech")
				.company("Amazon")
				.email("kitkat@gmail.com")
				.pNumber("111222333")
				.isAdmin(true)
				.tournaments(List.of())
				.build();
            userRepository.saveAll(List.of(user1, user2, user3));
        };
    }
}
