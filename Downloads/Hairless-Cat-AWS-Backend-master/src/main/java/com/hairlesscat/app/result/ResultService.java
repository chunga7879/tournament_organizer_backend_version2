package com.hairlesscat.app.result;

import com.hairlesscat.app.match.Match;
import com.hairlesscat.app.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ResultService {

	private final ResultRepository resultRepository;

	@Autowired
	public ResultService(ResultRepository resultRepository) {
		this.resultRepository = resultRepository;
	}

	public List<Result> getResults() {
		return resultRepository.findAll();
	}

	public Optional<Result> getResultById(Long resultId) {
		return resultRepository.findById(resultId);
	}

	public List<Result> getResultByMatch(Long mid) {
		return  resultRepository.findByMatch(mid);
	}


	public Result createResult(List<Team> rankedTeamArr, Match match) {
		Result result = Result.builder()
			.rankedTeam(rankedTeamArr)
			.match(match)
			.build();

		return resultRepository.save(result);
	}

	public void deleteResultByMatch(Long mid) {
		List<Result> results = getResultByMatch(mid);
		if (results.isEmpty() || results == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No result found with given match " + mid);
		}
		Result r = results.get(0);
		resultRepository.delete(r);
	}

}
