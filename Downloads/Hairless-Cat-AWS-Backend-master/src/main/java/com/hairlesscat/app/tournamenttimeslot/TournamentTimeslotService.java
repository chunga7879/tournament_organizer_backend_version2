package com.hairlesscat.app.tournamenttimeslot;

import com.hairlesscat.app.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentTimeslotService {

    final private TournamentTimeslotRepository tournamentTimeslotRepository;

    @Autowired
    public TournamentTimeslotService(TournamentTimeslotRepository tournamentTimeslotRepository) {
        this.tournamentTimeslotRepository = tournamentTimeslotRepository;
    }

    public List<TournamentTimeslot> generateThirtyMinuteTimeslotsFromStartAndEndTime(LocalDateTime tournamentStartTime, LocalDateTime tournamentEndTime) {

        LocalDateTime timeslotStartTime = tournamentStartTime;
        List<TournamentTimeslot> timeslots = new ArrayList<>();
        while (timeslotStartTime.isBefore(tournamentEndTime)) {
            // We assume that each timeslot is only 30mins
            LocalDateTime timeslotEndtime = timeslotStartTime.plusMinutes(30);
            if (timeslotEndtime.isAfter(tournamentEndTime)) {
                timeslotEndtime = tournamentEndTime;
            }
            TournamentTimeslot timeslot = new TournamentTimeslot(timeslotStartTime, timeslotEndtime);

            timeslots.add(timeslot);
            timeslotStartTime = timeslotEndtime;
        }
        return timeslots;
    }

    public List<TournamentTimeslot> getTimeslotsForTeam(Team team) {
        return tournamentTimeslotRepository.findTournamentTimeslotsByAvailableTeamsContaining(team);
    }

	public Optional<TournamentTimeslot> getTournamentTimeslotsById(Long ts) {
		return tournamentTimeslotRepository.findById(ts);
	}
}
