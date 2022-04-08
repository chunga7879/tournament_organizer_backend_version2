package com.hairlesscat.app.team;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TeamConfig {
    @Bean("team_init")
    CommandLineRunner commandLineRunner(TeamRepository teamRepository) {
        return args -> {
            Team t1 = Team.builder().teamName("hairless").build();
            Team t2 = Team.builder().teamName("amazon").build();
            teamRepository.saveAll(List.of(t1, t2));
        };
    }
}
