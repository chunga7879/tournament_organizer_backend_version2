package com.hairlesscat.app.teammember;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    @Autowired
    public TeamMemberService(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    public List<Long> findAllTeamIdsByUserId(String userId) {
        return teamMemberRepository.findAllTeamIdsByUserId(userId);
    }

    public List<Long> getAllTeamsOfLeader(String userId) {
        return teamMemberRepository.getAllTeamsOfLeader(userId);
    }

	public Optional<TeamMember> getTeamMemberByUserIdTeamId(String userId, Long teamId) {
		return teamMemberRepository.findByIds(userId, teamId);
	}

    public List<Long> findTimeslotIdsByUserIdTeamId(String userId, Long teamId) {
        return teamMemberRepository.findTimeslotIdsByUserIdTeamId(userId, teamId);
    }

    @Transactional
    public boolean setMemberAvailabilities(Set<TeamMember> teamMembers, String userId, Set<Long> timeslotIds) {
        boolean isSuccessful = false;
        for (TeamMember member : teamMembers) {
            if (member.getUser().getUserId().equals(userId)) {
                member.setUserIndicatedTimeslotIds(Set.copyOf(timeslotIds));
                member.setIndicatedAvailabilities(true);
                isSuccessful = true;
                break;
            }
        }
        return isSuccessful;
    }

	@Transactional
	public void deleteUserTimeslots(TeamMember teamMember) {
		Set<Long> timeslotIds = new HashSet<>();
		teamMember.setUserIndicatedTimeslotIds(timeslotIds);
		teamMember.setIndicatedAvailabilities(false);
	}
}
