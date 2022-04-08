//package com.hairlesscat.app.tournament;
//
//import com.hairlesscat.app.schedule.Schedule;
//import com.hairlesscat.app.schedule.ScheduleRepository;
//import com.hairlesscat.app.tournamenttimeslot.TournamentTimeslot;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Configuration
//public class TournamentConfig {
//
//	@Bean(name = "tournament_init")
//    CommandLineRunner commandLineRunner(TournamentRepository tournamentRepository, ScheduleRepository scheduleRepository) {
//
//       return args -> {
//            TournamentParameter tournamentParameter1 = TournamentParameter.builder()
//                    .minNumberOfTeams(4)
//                    .maxNumberOfTeams(4)
//                    .minNumberOfPlayersPerTeam(1)
//                    .maxNumberOfPlayersPerTeam(1)
//                    .tournamentStyle(TournamentStyle.ROUND_ROBIN)
//                    .tournamentType(TournamentType.SINGLE)
//                    .build();
//
//            TournamentTimeslot timeslot1 = new TournamentTimeslot(LocalDateTime.parse("2022-03-11T08:00:00"), LocalDateTime.parse("2022-03-11T08:30:00"));
//
//            TournamentTimeslot timeslot2 = new TournamentTimeslot(LocalDateTime.parse("2022-03-11T08:30:00"), LocalDateTime.parse("2022-03-11T09:00:00"));
//
//            TournamentTimeslot timeslot3 = new TournamentTimeslot(LocalDateTime.parse("2022-03-11T09:00:00"), LocalDateTime.parse("2022-03-11T09:30:00"));
//
//            TournamentTimeslot timeslot4 = new TournamentTimeslot(LocalDateTime.parse("2022-03-11T09:30:00"), LocalDateTime.parse("2022-03-11T10:00:00"));
//
//            Schedule schedule1 = Schedule.builder()
//                    .tournamentStartTime(LocalDateTime.parse("2022-03-11T08:00:00"))
//                    .tournamentEndTime(LocalDateTime.parse("2022-03-11T10:00:00"))
//                    .timeslots(List.of(timeslot1, timeslot2, timeslot3, timeslot4))
//                    .build();
//
//            timeslot1.setSchedule(schedule1);
//            timeslot2.setSchedule(schedule1);
//            timeslot3.setSchedule(schedule1);
//            timeslot4.setSchedule(schedule1);
//
//           Tournament tournament1 = new Tournament( "Ping Pong", "A ping pong tournament", tournamentParameter1, schedule1);
//
//            tournamentRepository.saveAll(List.of(tournament1));
//		};
//    }
//}
