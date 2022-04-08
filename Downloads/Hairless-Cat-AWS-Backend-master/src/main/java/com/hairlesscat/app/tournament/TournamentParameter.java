package com.hairlesscat.app.tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.hairlesscat.app.view.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(
        name = "tournament_parameter"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentParameter {
    @Id
    @SequenceGenerator(
            name = "tournament_param_sequence",
            sequenceName = "tournament_param_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tournament_param_sequence")
    @Column(name = "tournament_parameter_id")
    @JsonProperty("tournament_parameter_id")
    @JsonView(Views.Public.class)
    private Long tournamentParameterId;
  
    @JsonView(Views.TournamentParameterFull.class)
  	@Min(value = 2, message = "Min number of teams must be at least 2")
  	@JsonProperty("min_number_of_teams")
    private int minNumberOfTeams;
  
    @JsonView(Views.TournamentParameterFull.class)
	  @Min(value = 2, message = "Max number of teams must be at least 2")
    @JsonProperty("max_number_of_teams")
    private int maxNumberOfTeams;
  
    @JsonView(Views.TournamentParameterFull.class)
	  @Min(value = 1, message = "Min number of players per team must be at least 1")
    @JsonProperty("min_number_of_players_per_team")
    private int minNumberOfPlayersPerTeam;
  
	  @Min(value = 1,  message = "Max number of players per team must be at least 1")
    @JsonView(Views.TournamentParameterFull.class)
    @JsonProperty("max_number_of_players_per_team")
    private int maxNumberOfPlayersPerTeam;

    @JsonView(Views.TournamentParameterSummary.class)
    @JsonProperty("tournament_type")
    private TournamentType tournamentType;

    @JsonView(Views.TournamentParameterSummary.class)
    @JsonProperty("tournament_style")
    private TournamentStyle tournamentStyle;
}
