package com.hairlesscat.app.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends  JpaRepository<Team, Long>{

	List<Team> findAllByTournament_TournamentId(Long tournamentId);

	@Query(value = "SELECT tournament_id FROM team WHERE team_id = ?1", nativeQuery = true)
	List<Long> findTournamentIdByTeamId(Long teamId);
}
