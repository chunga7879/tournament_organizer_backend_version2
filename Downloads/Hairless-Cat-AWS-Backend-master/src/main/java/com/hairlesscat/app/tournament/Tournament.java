package com.hairlesscat.app.tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.hairlesscat.app.match.Match;
import com.hairlesscat.app.schedule.Schedule;
import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.user.User;
import com.hairlesscat.app.util.Status;
import com.hairlesscat.app.view.Views;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tournament")
@Data
@NoArgsConstructor
public class Tournament {

    @Id
    @SequenceGenerator(
            name = "tournament_sequence",
            sequenceName = "tournament_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tournament_sequence")
    @Column(name = "tournament_id", updatable = false)
    @JsonProperty("tournament_id")
    @JsonView(Views.Public.class)
    private Long tournamentId;

    @Column(name = "tournament_name", columnDefinition = "TEXT")
    @NotBlank(message = "Tournament must have a name.")
    @JsonView(Views.TournamentSummary.class)
    private String name;

    @Column(name = "tournament_description", columnDefinition = "TEXT")
    @JsonView(Views.TournamentSummary.class)
    private String description;

    @Column(name = "status", nullable = false, columnDefinition = "TEXT")
    @JsonView(Views.TournamentSummary.class)
    private Status status = Status.NOT_STARTED;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(Views.TournamentFull.class)
    private Set<Team> teams = new HashSet<>();

    @OneToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "tournament_parameter_id",
            referencedColumnName = "tournament_parameter_id"
    )
    @JsonProperty("tournament_parameters")
    @NotNull(message = "Tournament parameters must be specified.")
    @JsonView(Views.TournamentSummary.class)
    private TournamentParameter tournamentParameter;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "tournament_schedule_id",
            referencedColumnName = "schedule_id"
    )
    @JsonProperty("tournament_schedule")
    @NotNull
    @JsonView(Views.TournamentSummary.class)
    private Schedule schedule;

	@OneToMany(
		mappedBy = "tournament",
		cascade = CascadeType.ALL
	)
    @JsonView(Views.TournamentFull.class)
	List<Match> matches;

	@ManyToOne
	@JoinColumn(
		name = "tournament_admin_user_id",
		referencedColumnName = "user_id")
	@JsonProperty("admin_user")
    @JsonView(Views.TournamentFull.class)
	private User adminUser;

    public Tournament(String name, String description, TournamentParameter tournamentParameter, Schedule schedule) {
        this.name = name;
        this.description = description;
        this.tournamentParameter = tournamentParameter;
        this.schedule = schedule;
    }

	// This has the user field so that we can GET tournaments associated with certain adminUser only
	public Tournament(User adminUser, String name, String description, TournamentParameter tournamentParameter, Schedule schedule) {
			this.adminUser = adminUser;
			this.name = name;
			this.description = description;
			this.tournamentParameter = tournamentParameter;
			this.schedule = schedule;
	}

    public void addTeam(Team team) {
        teams.add(team);
    }

    public boolean containsTeam(Team team) {
        return teams.contains(team);
    }
}
