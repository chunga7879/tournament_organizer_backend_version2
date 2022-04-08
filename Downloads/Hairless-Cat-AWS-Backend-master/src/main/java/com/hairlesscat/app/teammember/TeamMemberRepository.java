package com.hairlesscat.app.teammember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {

	@Query(value = "SELECT team_id FROM team_member WHERE user_id = ?1", nativeQuery = true)
	List<Long> findAllTeamIdsByUserId(String userId);

	@Query(value = "SELECT team_id FROM team_member WHERE user_id = ?1 AND is_leader = true", nativeQuery = true)
	List<Long> getAllTeamsOfLeader(String userId);

	@Query(value = "SELECT * FROM team_member WHERE user_id = ?1 AND team_id = ?2", nativeQuery = true)
	Optional<TeamMember> findByIds(String userId, Long teamId);

	@Query(value = "SELECT user_indicated_timeslot_ids FROM team_member_user_indicated_timeslot_ids WHERE team_member_user_id = ?1 AND team_member_team_id = ?2", nativeQuery = true)
	List<Long> findTimeslotIdsByUserIdTeamId(String userId, Long teamId);
}
