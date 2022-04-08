package com.hairlesscat.app.match;

import com.fasterxml.jackson.annotation.JsonView;
import com.hairlesscat.app.schedule.Schedule;
import com.hairlesscat.app.schedule.ScheduleErrorException;
import com.hairlesscat.app.schedule.ScheduleNotStartedException;
import com.hairlesscat.app.schedule.ScheduleService;
import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.team.TeamService;
import com.hairlesscat.app.tournament.Tournament;
import com.hairlesscat.app.tournament.TournamentService;
import com.hairlesscat.app.user.UserService;
import com.hairlesscat.app.util.ResponseWrapper;
import com.hairlesscat.app.util.TeamStatus;
import com.hairlesscat.app.view.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/matches")
public class MatchController {
    private final MatchService matchService;
    private final TeamService teamService;
    private final TournamentService tournamentService;
    private final ScheduleService scheduleService;
    private final UserService userService;

    @Autowired
    public MatchController(MatchService matchService, TeamService teamService, TournamentService tournamentService, ScheduleService scheduleService, UserService userService) {
        this.matchService = matchService;
        this.teamService = teamService;
        this.tournamentService = tournamentService;
        this.scheduleService = scheduleService;
        this.userService = userService;
    }

    @GetMapping()
    @JsonView(Views.MatchFull.class)
    public Map<String, List<Match>> getAllMatches() {
        List<Match> matches = matchService.getAllMatches();
        Map<String, List<Match>> match = new HashMap<>();
        match.put("matches", matches);
        return match;
    }

    @GetMapping(path = "{match_id}")
    @JsonView(Views.MatchFull.class)
    public ResponseEntity<Match> getMatchByMatchId(
            @PathVariable("match_id") Long matchId) {
        Match match = matchService.getMatchByMatchId(matchId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No match found with id " + matchId));
        return ResponseEntity.ok(match);
    }

    @GetMapping(params = {"tournament_id"})
    @JsonView(Views.MatchFull.class)
    public ResponseEntity<Map<String, List<Match>>> getMatchesByTournament(@RequestParam(value = "tournament_id") Long tournamentId) {

        Tournament tournament = tournamentService
                .getTournamentByTournamentId(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No tournament found with id " + tournamentId));

        Schedule schedule = tournament.getSchedule();

        try {
            List<Match> matches = scheduleService.getMatches(schedule);
//            if (matches.isEmpty()) {
//                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No match found with tournament id " + tournamentId);
//            } else {
                return ResponseEntity.ok(ResponseWrapper.wrapResponse("matches", matches));
 //           }
			// front end would rather receive empty matches
		} catch (ScheduleNotStartedException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament has no matches because the scheduling algorithm has not been started.");
        } catch (ScheduleErrorException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Tournament has no matches because something went wrong during scheduling. Reason: " + e.getMessage());
        }
    }

    @GetMapping(params = "team_id")
    @JsonView(Views.MatchFull.class)
    public Map<String, List<Match>> getMatchesByTeamId(@RequestParam(value = "team_id") Long teamId) {
        Team team = teamService
                .getTeamById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No team found with id " + teamId));
        List<Match> matches = matchService.getMatchesOfTeam(team);
//        if (matches.isEmpty()) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    String.format("No matches associated with team %s [id: %d]", team.getTeamName(), team.getTeamId()));
//        }
		// front end would rather receive empty matches
        return ResponseWrapper.wrapResponse("matches", matches);
    }


    @GetMapping(params = "{user_id}")
    @JsonView(Views.MatchFull.class)
    public Map<String, List<Match>> getMatchesByAdminUserId(@RequestParam(value = "user_id") String userId) {
        if (!userService.validateUser(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with id " + userId);
        }
        List<Match> matches = matchService.getMatchesByUserId(userId);
        return ResponseWrapper.wrapResponse("matches", matches);
    }

    @GetMapping(params = {"team_id", "team_status"})
    @JsonView(Views.MatchFull.class)
    public List<Match> getMatchesByTeamIdAndTeamStatus(
            @RequestParam("team_id") Long teamId,
            @RequestParam("team_status") String teamStatus) {

        try {
            TeamStatus ts = TeamStatus.valueOf(teamStatus.toUpperCase());
            Team team = teamService
                    .getTeamById(teamId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No team found with id " + teamId));

            return matchService.getAllMatchesOfTeamWithTeamStatus(team, ts);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, teamStatus + " is not a valid team status.");
        }
    }

    @GetMapping(params = "match_status")
    @JsonView(Views.MatchFull.class)
    public Map<String, List<Match>> getMatchesWithMatchStatus(@RequestParam("match_status") String status) {
        try {
            MatchStatus matchStatus = MatchStatus.valueOf(status.toUpperCase());
            List<Match> matches = matchService.getAllMatchesWithStatus(matchStatus);
            return ResponseWrapper.wrapResponse("matches", matches);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, status + " is not a valid status.");
        }
    }

    @PostMapping
    public Match createMatch(@RequestBody Match match) {
        return matchService.createMatch(match);
    }

	@PostMapping(path = "{match_id}/complete")
	@JsonView(Views.MatchFull.class)
	public ResponseEntity<Match> changeMatchStatus(@PathVariable("match_id") Long match_id) {

		matchService.setMatchStatusComplete(match_id);

		Match match = matchService.getMatchByMatchId(match_id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No match found with id " + match_id));

		return ResponseEntity.ok(match);
	}

    @PostMapping(path = "actions/{match_id}/accept_match/{team_id}")
    public ResponseEntity<String> acceptMatch(@PathVariable("match_id") Long matchId,
                                              @PathVariable("team_id") Long teamId) {
        Match match = matchService.getMatchByMatchId(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No match found with id " + matchId));
        Team team = teamService.getTeamById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No team found with id " + teamId));

        matchService.acceptMatch(team, match);

        return ResponseEntity.ok("Team of id " + teamId + " has accepted the match of id " + matchId);
    }

    @DeleteMapping(path = "{mid}")
    public void deleteMatch(@PathVariable("mid") Long mid) {
        matchService.deleteMatch(mid);
    }

}
