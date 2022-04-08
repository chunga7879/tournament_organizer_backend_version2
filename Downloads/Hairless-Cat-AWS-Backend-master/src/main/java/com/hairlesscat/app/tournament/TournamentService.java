package com.hairlesscat.app.tournament;

import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.tournamenttimeslot.TournamentTimeslot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public List<Tournament> getTournaments() {
        return tournamentRepository.findAll();
    }

    public Optional<Tournament> getTournamentByTournamentId(Long tournamentId) {
        return tournamentRepository.findById(tournamentId);
    }

    public void deleteTournament(Long tournamentId) {
        tournamentRepository.deleteById(tournamentId);
    }

    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public List<TournamentTimeslot> getTimeslotsFromTournament(Tournament tournament) {
        return tournament.getSchedule().getTimeslots();
    }

    public List<Tournament> getTournamentsByUserId(String userId) {
        return tournamentRepository.findAllByAdminUser_UserId(userId);
	}


    public Tournament addTeamsToTournament(Tournament tournament, List<Team> teams) {
        for (Team t : teams) {
            tournament.getTeams().add(t);
            t.setTournament(tournament);
        }
        return tournamentRepository.save(tournament);
    }

    public Tournament deleteTeamFromTournament(Tournament tournament, Team team) {
        tournament.getTeams().remove(team);
        return tournamentRepository.save(tournament);
    }

    public boolean validateTeamInTournament(Tournament tournament, Team team) {
        return tournament.containsTeam(team);
    }

    public List<Tournament> filterForUnscheduledTournaments(List<Tournament> tournaments) {
        return tournaments
                .stream()
                .filter(tournament -> !tournament.getSchedule().isScheduled())
                .toList();
    }

	public List<Tournament> filterForScheduledTournaments(List<Tournament> tournaments) {
		return tournaments
			.stream()
			.filter(tournament -> tournament.getSchedule().isScheduled())
			.toList();
	}
}
