package com.hairlesscat.app.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.hairlesscat.app.match.Match;
import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.view.Views;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "result")
@Data
@NoArgsConstructor
@Builder
public class Result {
	@Id
	@SequenceGenerator(
		name = "result_sequence_generator",
		sequenceName = "result_sequence_generator",
		allocationSize = 1
	)
	@GeneratedValue(
		generator = "result_sequence_generator",
		strategy = GenerationType.SEQUENCE
	)
	@Column(name = "result_id", updatable = false)
	@JsonProperty("result_id")
	@JsonView(Views.Public.class)
	private Long resultId;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(
		name= "result_teams",
		joinColumns = @JoinColumn(name = "team_id"),
		inverseJoinColumns = @JoinColumn(name = "result_id"))
	@JsonView(Views.ResultSummary.class)
	@JsonProperty("team_results")
	private List<Team> rankedTeam;

	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(
		name = "result_match_id",
		referencedColumnName = "match_id"
	)
	@JsonProperty("result_match")
	@JsonView(Views.ResultFull.class)
	private Match match;

	public Result(List<Team> rankedTeamArr, Match match){
		this.rankedTeam = rankedTeamArr;
		this.match = match;
	}

	public Result(Long resultId, List<Team> rankedTeamArr, Match match){
		this.rankedTeam = rankedTeamArr;
		this.match = match;
	}
}
