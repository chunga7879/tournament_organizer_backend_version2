package com.hairlesscat.app.team;

import com.hairlesscat.app.tournament.Tournament;
import com.hairlesscat.app.teammember.TeamMember;
import com.hairlesscat.app.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class TeamService {
    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Optional<Team> getTeamById(Long teamId) {
        return teamRepository.findById(teamId);
    }

    public List<Team> getTeams() {
        return teamRepository.findAll();
    }

    public Team createTeam(Tournament tournament, String teamName, User teamLeader) {
        Team team = Team.builder()
                .tournament(tournament)
                .teamName(teamName)
                .build();

        TeamMember leader = new TeamMember(teamLeader, team, true);
        team.addTeamMember(leader);
        return teamRepository.save(team);
    }

    public List<Long> getTournamentIdByTeamId(Long teamId) {
        return teamRepository.findTournamentIdByTeamId(teamId);
    }

    public void deleteTeam(Long teamId) {
        boolean exists = teamRepository.existsById(teamId);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No team found with id " + teamId);
        } else {
            teamRepository.deleteById(teamId);
        }
    }

    @Transactional
    public void updateTeam(Long teamId, String teamName) {
        Team team = teamRepository
                .findById(teamId)
                .orElseThrow(() -> new IllegalStateException("team with id " + teamId + " does not exist"));

        if (teamName != null && !teamName.isEmpty()) {
            team.setTeamName(teamName);
        }
    }

    public List<User> getUsers(Team team) {
        return team
                .getTeamMembers()
                .stream()
                .map(TeamMember::getUser)
                .toList();
    }

    public Team addUsers(Team team, List<User> users) {
        for (User user : users) {
            TeamMember teamMember = new TeamMember(user, team, false);
            team.addTeamMember(teamMember);
        }
        return teamRepository.save(team);
    }

    public Team deleteUserFromTeam(Team team, User user) {
        team.getTeamMembers().removeIf(member -> member.getUser().equals(user));
        return teamRepository.save(team);
    }

    public List<Team> getTeamsOfTournament(Long tournamentId) {
        return teamRepository.findAllByTournament_TournamentId(tournamentId);
    }

    public List<Long> aggregateTeamMemberAvailabilities(Set<TeamMember> teamMembers, int minNumberOfPlayersRequired) {
        // Store the count of members available for a particular timeslot
        Map<Long, Integer> timeslotCountMap = new HashMap<>();

        // Iterate through each teamMember and collect their available timings
        for (TeamMember teamMember : teamMembers) {
            for (Long timeslotId : teamMember.getUserIndicatedTimeslotIds()) {
                int currentCount = timeslotCountMap.getOrDefault(timeslotId, 0);
                timeslotCountMap.put(timeslotId, currentCount + 1);
            }
        }

        List<Long> validTimeslotIdsForTeam = new ArrayList<>();
        for (Long timeslotId : timeslotCountMap.keySet()) {
            // Get all the timeslots that have enough team members at that time
            if (timeslotCountMap.get(timeslotId) >= minNumberOfPlayersRequired) {
                validTimeslotIdsForTeam.add(timeslotId);
            }
        }

        return validTimeslotIdsForTeam;
    }

    public boolean validateAllTeamMembersIndicatedAvailabilities(Set<TeamMember> teamMembers) {
        return teamMembers
                .stream()
                .map(TeamMember::hasIndicatedAvailabilities)
                .reduce(true, (accumulated, current) -> accumulated && current);
    }

    public boolean teamHasMember(Team team, String userId) {
        return team.getTeamMembers()
                .stream()
                .reduce(
                        false,
                        (accumulated, current) -> accumulated || current.getUser().getUserId().equals(userId),
                        (x, y) -> x || y);
    }

    public Optional<TeamMember> getTeamLeader(Team team) {
        // Assumes that there is only one team leader. Might be a bad design especially since
        // there is nothing to check if there is really one team leader.
        return team.getTeamMembers().stream().filter(TeamMember::getIsLeader).findFirst();
    }
}
