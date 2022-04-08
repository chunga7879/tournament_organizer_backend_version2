package com.hairlesscat.app.team;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.hairlesscat.app.match.Match;
import com.hairlesscat.app.result.Result;
import com.hairlesscat.app.teammember.TeamMember;
import com.hairlesscat.app.tournament.Tournament;
import com.hairlesscat.app.view.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity()
@Table(name = "team")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Team {
    @Id
    @SequenceGenerator(
            name = "team_sequence",
            sequenceName = "team_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "team_sequence"
    )
    @Column(
            name = "team_id",
            updatable = false,
            nullable = false
    )
    @JsonProperty("team_id")
    @JsonView(Views.Public.class)
    private Long teamId;

    @ManyToOne
    @JoinColumn(name = "tournament_id", referencedColumnName = "tournament_id")
    @JsonView(Views.TeamExtended.class)
    private Tournament tournament;

    @Column(
            name = "team_name",
            columnDefinition = "TEXT"
    )
    @NotBlank
    @JsonProperty("team_name")
    @JsonView(Views.TeamSummary.class)
    private String teamName;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("team_members")
    @Builder.Default
    @JsonView(Views.TeamMembers.class)
    Set<TeamMember> teamMembers = new HashSet<>();

    @ManyToMany(mappedBy = "rankedTeam", cascade = CascadeType.ALL)
    @JsonView(Views.TeamFull.class)
    private List<Result> results;

    @ManyToMany(mappedBy = "teamsInMatch", cascade = CascadeType.PERSIST)
    @JsonView(Views.TeamFull.class)
    private List<Match> matches;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(teamId, team.teamId) && Objects.equals(tournament.getTournamentId(), team.tournament.getTournamentId()) && Objects.equals(teamName, team.teamName);
    }


    public void addTeamMember(TeamMember teamMember) {
        teamMembers.add(teamMember);
    }
}
