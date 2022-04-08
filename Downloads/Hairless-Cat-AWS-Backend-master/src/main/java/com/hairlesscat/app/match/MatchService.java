package com.hairlesscat.app.match;

import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.util.TeamStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Optional<Match> getMatchByMatchId(Long matchId) {
        return matchRepository.findById(matchId);
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public List<Match> getMatchByTournament(Long tournamentId) {
        return matchRepository.findAllByTournament_TournamentId(tournamentId);
    }

    public Match createMatch(Match match) {
        return matchRepository.save(match);
    }

    public void deleteMatch(Long mid) {
        boolean exists = matchRepository.existsById(mid);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No match found with this id " + mid);
        } else {
            matchRepository.deleteById(mid);
        }
    }

    public List<Match> getMatchesByUserId(String userId) {
        return matchRepository.findAllByAdminUser_UserId(userId);
    }

	public List<Match> getMatchesOfTeam(Team team) {
		return matchRepository.findAllByTeamsInMatchContaining(team);
	}

    public List<Match> getAllMatchesWithStatus(MatchStatus status) {
        return matchRepository.findAllByMatchStatusEquals(status);
    }

    public List<Match> getAllMatchesOfTeamWithTeamStatus(Team team, TeamStatus status) {
        return matchRepository.findAllByTeamWithTeamStatus(team, status.getValue());
    }

    public boolean checkIfAllTeamsAccepted(Match match) {
        for (TeamStatus status : match.getTeamStatusMap().values()) {
            if (status != TeamStatus.CONFIRMED) {
                return false;
            }
        }
        return true;
    }

    public void setMatchStatusUpcoming(Match match) {
        match.setMatchStatusUpcoming();
    }

    @Transactional
    public void acceptMatch(Team team, Match match) {

        Map<Team, TeamStatus> map = match.getTeamStatusMap();
        map.replace(team, TeamStatus.CONFIRMED);

        if (checkIfAllTeamsAccepted(match)) {
            setMatchStatusUpcoming(match);
        }
    }

	public void setMatchStatusComplete(Long match_id) {
		MatchStatus complete = MatchStatus.COMPLETED;
		matchRepository.setMatchStatusComplete(match_id, complete);
	}
}
