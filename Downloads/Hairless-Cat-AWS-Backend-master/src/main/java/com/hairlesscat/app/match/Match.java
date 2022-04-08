package com.hairlesscat.app.match;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hairlesscat.app.result.Result;
import com.hairlesscat.app.schedule.Schedule;
import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.tournament.Tournament;
import com.hairlesscat.app.user.User;
import com.hairlesscat.app.util.TeamStatus;
import com.hairlesscat.app.view.Views;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(
        name = "match"
)
@Data
@NoArgsConstructor
public class Match {
    @Id
    @SequenceGenerator(
            name = "match_sequence_generator",
            sequenceName = "match_sequence_generator",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "match_sequence_generator"
    )
    @Column(
            name = "match_id"
    )
    @JsonProperty("match_id")
    @JsonView(Views.Public.class)
    private Long matchId;


    @ManyToOne
    @JoinColumn(name = "tournament_id", referencedColumnName = "tournament_id")
    @JsonProperty("match_tournament")
    @JsonView(Views.MatchFull.class)
    private Tournament tournament;

    @ElementCollection
    @CollectionTable(
            name = "match_team_status_map",
            joinColumns = @JoinColumn(name = "match_id", referencedColumnName = "match_id"))
    @MapKeyJoinColumn(name = "team_id", referencedColumnName = "team_id")
    @Column(name = "team_status")
    @JsonView(Views.MatchFull.class)
    @JsonProperty("team_statuses")
    @JsonSerialize(using = TeamStatusSerializer.class)
    @JsonDeserialize(using = TeamStatusDeserializer.class)
    private Map<Team, TeamStatus> teamStatusMap = new HashMap<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "match_result_id",
            referencedColumnName = "result_id"
    )
    @JsonProperty("match_result")
    @JsonView(Views.MatchFull.class)
    private Result result;

    @ManyToOne
    @JoinColumn(
            name = "match_admin_user_id",
            referencedColumnName = "user_id")
    @JsonProperty("match_admin_user")
    @JsonView(Views.MatchSummary.class)
    private User adminUser;

    @Column(
            name = "match_status"
    )
    @JsonView(Views.MatchSummary.class)
    @JsonProperty("match_status")
    private MatchStatus matchStatus = MatchStatus.PENDING_TEAM_CONFIRMATION;

    @JsonProperty("match_start_time")
    @JsonView(Views.MatchSummary.class)
    private LocalDateTime matchStartTime;

    @JsonProperty("match_end_time")
    @JsonView(Views.MatchSummary.class)
    private LocalDateTime matchEndTime;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "match_teams",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "match_id"))
    @JsonView(Views.MatchFull.class)
    @JsonProperty("teams_in_match")
    private List<Team> teamsInMatch;

    @ManyToOne
    @JoinColumn(
            name = "schedule_id",
            referencedColumnName = "schedule_id"
    )
    @JsonIgnore
    private Schedule schedule;


    public Match(List<Team> teamsInMatch, LocalDateTime startTime, LocalDateTime endTime) {
        this.teamsInMatch = teamsInMatch;
        this.matchStartTime = startTime;
        this.matchEndTime = endTime;

        Map<Team, TeamStatus> teamStatusMap = new HashMap<>();
        for (Team team : teamsInMatch) {
            teamStatusMap.put(team, TeamStatus.PENDING);
        }
        this.teamStatusMap = teamStatusMap;
    }

    public Match(Long matchId, Tournament tournament, MatchStatus status, Map<Team, TeamStatus> teamStatusMap, Result result, User user) {
        this.matchId = matchId;
        this.tournament = tournament;
        this.matchStatus = status;
        this.teamStatusMap = teamStatusMap;
        this.result = result;
        this.adminUser = user;
    }

    public Match setSchedule(Schedule schedule) {
		this.schedule = schedule;
		return this;
	}

	public Match setTournament(Tournament tournament) {
		this.tournament = tournament;
		return this;
	}

    public void setMatchStatusUpcoming() {
        this.matchStatus = MatchStatus.UPCOMING;
    }
}
