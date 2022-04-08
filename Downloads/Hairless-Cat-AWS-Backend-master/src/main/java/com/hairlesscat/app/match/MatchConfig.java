//package com.hairlesscat.app.match;
//
//import com.hairlesscat.app.team.Team;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Configuration
//public class MatchConfig {
//    @Bean(name = "match_init")
//    CommandLineRunner commandLineRunner(MatchRepository matchRepository) {
//        return args -> {
//            Team team1 = Team.builder().teamName("dogs").build();
//            Team team2 = Team.builder().teamName("cats").build();
//            Team team3 = Team.builder().teamName("birds").build();
//            Match match1 = new Match(List.of(team1, team2), LocalDateTime.now(), LocalDateTime.now());
//            Match match2 = new Match(List.of(team1, team3), LocalDateTime.now(), LocalDateTime.now());
//            matchRepository.saveAll(List.of(match1, match2));
//        };
//    }
//}
