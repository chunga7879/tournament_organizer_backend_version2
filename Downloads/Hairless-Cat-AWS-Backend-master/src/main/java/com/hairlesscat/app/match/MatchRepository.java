package com.hairlesscat.app.match;

import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.util.TeamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByTournament_TournamentId(Long tournamentId);

    List<Match> findAllByTeamsInMatchContaining(Team team);

	List<Match> findAllByAdminUser_UserId(String userId);

	List<Match> findAllByMatchStatusEquals(MatchStatus matchStatus);

	@Query(value = "SELECT * FROM match NATURAL JOIN (SELECT * FROM match_team_status_map as mts WHERE mts.team_id = ?1 AND mts.team_status = ?2) as temp", nativeQuery = true)
	List<Match> findAllByTeamWithTeamStatus(Team team, int teamStatus);

	@Modifying
	@Transactional
	@Query("UPDATE Match m SET m.matchStatus = ?2 WHERE m.matchId = ?1")
	void setMatchStatusComplete(Long id, MatchStatus status);
}
