package com.hairlesscat.app.tournament;

import com.fasterxml.jackson.annotation.JsonView;

import com.hairlesscat.app.algorithm.ImperfectMatchingException;
import com.hairlesscat.app.algorithm.MoreMatchesThanAvailableTimeslotsException;
import com.hairlesscat.app.match.Match;
import com.hairlesscat.app.schedule.Schedule;
import com.hairlesscat.app.schedule.ScheduleService;
import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.team.TeamService;
import com.hairlesscat.app.teammember.TeamMemberService;
import com.hairlesscat.app.tournamenttimeslot.TournamentTimeslot;
import com.hairlesscat.app.tournamenttimeslot.TournamentTimeslotService;
import com.hairlesscat.app.user.User;
import com.hairlesscat.app.user.UserService;
import com.hairlesscat.app.util.ResponseWrapper;
import com.hairlesscat.app.view.Views;
import com.hairlesscat.app.validation.MissingFieldsException;
import com.hairlesscat.app.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {
    private final TournamentService tournamentService;
    private final ScheduleService scheduleService;
    private final TournamentTimeslotService tournamentTimeslotService;
    private final TeamService teamService;
    private final UserService userService;
    private final TeamMemberService teamMemberService;

    public TournamentController(TournamentService tournamentService, ScheduleService scheduleService, TournamentTimeslotService tournamentTimeslotService, TeamService teamService, UserService userService, TeamMemberService teamMemberService) {
        this.tournamentService = tournamentService;
        this.scheduleService = scheduleService;
        this.tournamentTimeslotService = tournamentTimeslotService;
        this.teamService = teamService;
        this.userService = userService;
        this.teamMemberService = teamMemberService;
    }

    @GetMapping
    @JsonView(Views.TournamentSummary.class)
    public ResponseEntity<Map<String, List<Tournament>>> getTournaments(
            @RequestParam(value = "user_id", required = false) String userId,
            @RequestParam(value = "unscheduled_only", required = false) boolean unscheduledQueryParamIsSet,
			@RequestParam(value = "scheduled_only", required = false) boolean scheduledQueryParamIsSet) {

        List<Tournament> tournaments;

        if (userId == null) {
            tournaments = tournamentService.getTournaments();
        } else {
            tournaments = tournamentService.getTournamentsByUserId(userId);
        }

        if (unscheduledQueryParamIsSet) {
            tournaments = tournamentService.filterForUnscheduledTournaments(tournaments);
        }
		if (scheduledQueryParamIsSet){
			tournaments = tournamentService.filterForScheduledTournaments(tournaments);
		}


        return ResponseEntity.ok(ResponseWrapper.wrapResponse("tournaments", tournaments));
    }

    @GetMapping(path = "{tournament_id}")
    @JsonView(Views.TournamentFull.class)
    public ResponseEntity<Tournament> getTournamentById(@PathVariable(value = "tournament_id") Long tournamentId) {
        Tournament tournament = tournamentService
                .getTournamentByTournamentId(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No tournament found with id " + tournamentId));
        return ResponseEntity.ok(tournament);
    }

    @DeleteMapping(params = {"tournament_id"})
    public Long deleteTournament(@RequestParam(value = "tournament_id") Long tournamentId) {
        try {
            tournamentService.deleteTournament(tournamentId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        return tournamentId;
    }

    @PostMapping
    //TODO: validation error handling
    @JsonView(Views.TournamentFull.class)
    public ResponseEntity<Tournament> createTournament(
            @RequestParam(value = "user_id", required = false) Optional<String> optionalUserId,
            @Valid @RequestBody Tournament tournament) {

        optionalUserId.ifPresent(userId -> {
            if (userId.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided user_id must not be empty");
            }
            User user = userService
                    .getUserById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot create tournament because no user found with id " + userId));
            tournament.setAdminUser(user);
        });

        Schedule schedule = tournament.getSchedule();
        List<TournamentTimeslot> timeslots = tournamentTimeslotService.generateThirtyMinuteTimeslotsFromStartAndEndTime(schedule.getTournamentStartTime(), schedule.getTournamentEndTime());
        scheduleService.addTimeslotsToSchedule(schedule, timeslots);
        return ResponseEntity.ok(tournamentService.createTournament(tournament));
    }


    @GetMapping(path = "{tournament_id}/timeslots")
    @JsonView(Views.TournamentTimeslot.class)
    public ResponseEntity<Map<String, Object>> getTimeslotsByTournamentId(@PathVariable(value = "tournament_id") Long tournamentId) {
        Tournament tournament = tournamentService
                .getTournamentByTournamentId(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No tournament found with id " + tournamentId));
        List<TournamentTimeslot> timeslots = tournamentService.getTimeslotsFromTournament(tournament);
        Map<String, Object> body = new HashMap<>();
        body.put("tournament_id", tournamentId);
        body.put("timeslots", timeslots);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping(path = "{tournament_id}/teams/{team_id}")
    @JsonView(Views.TournamentFull.class)
    public ResponseEntity<Tournament> deleteTeamById(@PathVariable(value = "tournament_id") Long tournamentId, @PathVariable(value = "team_id") Long teamId) {
        Tournament tournament = tournamentService
                .getTournamentByTournamentId(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No tournament found with id " + tournamentId));

        Team team = teamService
                .getTeamById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No team found with id " + teamId));

        return ResponseEntity.ok(tournamentService.deleteTeamFromTournament(tournament, team));
    }

    @PostMapping(path = "actions/gen_match_schedule/{tournament_id}")
    public ResponseEntity<String> generateMatchSchedule(@PathVariable(value = "tournament_id") Long tournamentId) {
        Tournament tournament = tournamentService
                .getTournamentByTournamentId(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No tournament found with id " + tournamentId));
        Schedule schedule = tournament.getSchedule();

		Set<Team> teams = tournament.getTeams();

		if (teams.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No teams are found in tournament " + tournamentId);
		}

        int minNumRequiredTeams = tournament.getTournamentParameter().getMinNumberOfTeams();

        if (teams.size() < minNumRequiredTeams) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                            "The required number of teams in this tournament has not been met. Number of teams currently: %d, number of required teams: %d.",
                            teams.size(),
                            minNumRequiredTeams));
        }

		for(Team team : teams.stream().toList()) {
			List<TournamentTimeslot> ttss = tournamentTimeslotService.getTimeslotsForTeam(team);
			if(ttss.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				"Team "+ team.getTeamId() + " of tournament " + tournamentId + " has no indicated team availability.");
			}
		}

        if (schedule.getTimeslots() == null || schedule.getTimeslots().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tournament schedule configuration is not set properly or not fit for generating a match schedule. Please check again.");
        }

        try {
            List<Match> matches = scheduleService.generateMatchSchedule(schedule, List.copyOf(teams));
            matches
                    .stream()
                    .map(match -> match.setTournament(tournament))
                    .forEach(match -> match.setSchedule(schedule));
            Schedule savedSchedule = scheduleService.addMatchesToSchedule(schedule, matches);
            scheduleService.setScheduleSuccess(savedSchedule);
        } catch (ImperfectMatchingException | MoreMatchesThanAvailableTimeslotsException e) {
            scheduleService.setScheduleError(schedule, e.getMessage());
            scheduleService.resetTeamAvailabilities(schedule);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Running tournament scheduling algorithm.");
    }

    @PostMapping(path = "{tournament_id}/teams")
    @JsonView(Views.TeamFull.class)
    public ResponseEntity<Team> addTeamToTournament(
            @PathVariable("tournament_id") Long tournamentId,
            @RequestBody Map<String, String> requestBody) {

        try {
            Validator.requestBodyTopLevelFieldValidation(List.of("user_id", "team_name"), requestBody.keySet());
        } catch (MissingFieldsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        String userId = requestBody.get("user_id");
        String teamName = requestBody.get("team_name");

        User user = userService
                .getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot create a team because no user is found with id " + userId));

        Tournament tournament = tournamentService
                .getTournamentByTournamentId(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot create a team because no tournament is found with id " + tournamentId));

        return ResponseEntity.ok(teamService.createTeam(tournament, teamName, user));
    }

    @PostMapping(path = "{tournament_id}/schedule/timeslots/{team_id}")
    public ResponseEntity<String> addTeamToTimeslots(@PathVariable(value = "tournament_id") Long tournamentId, @PathVariable(value = "team_id") Long teamId, @RequestBody Map<String, Long[]> requestBody) {
        Tournament tournament = tournamentService
                .getTournamentByTournamentId(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No tournament found with id ." + tournamentId));

        Team team = teamService
                .getTeamById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No team found with id ." + teamId));

        if (!tournamentService.validateTeamInTournament(tournament, team)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Team does not exist in tournament.");
        }

        try {
            Validator.requestBodyTopLevelFieldValidation(List.of("timeslot_ids"), requestBody.keySet());
        } catch (MissingFieldsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        List<Long> timeslotIds = List.of(requestBody.get("timeslot_ids"));

        if (scheduleService.validateTimeslotIds(tournament.getSchedule(), timeslotIds)) {
            scheduleService.addTeamToTimeslots(tournament.getSchedule(), team, timeslotIds);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One or more timeslot ids provided does not belong to this tournament.");
        }
        return ResponseEntity.ok().body("Team availability added to tournament timeslots.");
    }

    @PostMapping(params = {"user_id"})
    @JsonView(Views.TournamentFull.class)
    public ResponseEntity<Tournament> createTournamentWithUserId(@RequestParam(value = "user_id") String userId, @RequestBody Tournament tournament) {
        User user = userService
                .getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot create tournament because no user found with id " + userId));
        tournament.setAdminUser(user);
        Schedule schedule = tournament.getSchedule();
        List<TournamentTimeslot> timeslots = tournamentTimeslotService.generateThirtyMinuteTimeslotsFromStartAndEndTime(schedule.getTournamentStartTime(), schedule.getTournamentEndTime());
        scheduleService.addTimeslotsToSchedule(schedule, timeslots);
        return ResponseEntity.ok(tournamentService.createTournament(tournament));
    }

}
