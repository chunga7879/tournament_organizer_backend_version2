package com.hairlesscat.app.result;

import com.fasterxml.jackson.annotation.JsonView;
import com.hairlesscat.app.match.Match;
import com.hairlesscat.app.match.MatchService;
import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.view.Views;
import com.hairlesscat.app.team.TeamService;
import com.hairlesscat.app.util.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/results")
public class  ResultController {
	private final ResultService resultService;
	private final MatchService matchService;
	private final TeamService teamService;

	@Autowired
	public ResultController(ResultService resultService, MatchService matchService, TeamService teamService) {
		this.resultService = resultService;
		this.matchService = matchService;
		this.teamService = teamService;
	}

	@GetMapping()
	@JsonView(Views.ResultSummary.class)
	public Map<String, List<Result>> getResults() {
		List<Result> results = resultService.getResults();
		Map<String, List<Result>> result = new HashMap<>();
		result.put("results", results);
		return result;
	}

	@GetMapping(path = "{result_id}")
	@JsonView(Views.ResultFull.class)
	public ResponseEntity<Result> getResultById(@PathVariable(value="result_id") Long resultId) {
		Result result =  resultService.getResultById(resultId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No result found with id " + resultId));
		return ResponseEntity.ok(result);
	}

	@GetMapping(path = "{match_id}/match")
	@JsonView(Views.ResultFull.class)
	public ResponseEntity<Result> getResultByMatchId(@PathVariable(value="match_id") Long matchId) {
		List<Result> results = resultService.getResultByMatch(matchId);
		if (results == null || results.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No match found with id " + matchId);
		}
		Result result = results.get(0);
		return ResponseEntity.ok(result);
	}

	@PostMapping(path = "{match_id}")
   	@JsonView(Views.ResultFull.class)
	public ResponseEntity<Result> createResult(@PathVariable(value="match_id") Long match_id,
											   @RequestBody Map<String,Long[]> requestBody) {
		Match match = matchService.getMatchByMatchId(match_id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No match found with id " + match_id));

		List<Long> orderedTeam_ids = List.of(requestBody.get("team_results"));
		List<Team> rankedTeams = new ArrayList<>();
		for(Long team_id : orderedTeam_ids) {
			Team team = teamService.getTeamById(team_id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No team found with id " + team_id));
			rankedTeams.add(team);
		}
		return ResponseEntity.ok(resultService.createResult(rankedTeams, match));
	}

	@DeleteMapping(path = "{match_id}")
	public void deleteResult(@PathVariable("match_id") Long match_id) {
		resultService.deleteResultByMatch(match_id);
	}

}
