package com.hairlesscat.app.teammember;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.user.User;
import com.hairlesscat.app.view.Views;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "team_member")
@NoArgsConstructor
@IdClass(TeamMemberId.class)
public class TeamMember {
    @Id
    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id"
    )
    @JsonView(Views.TeamMembers.class)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(
            name = "team_id",
            referencedColumnName = "team_id"
    )
    @JsonView(Views.TeamAssociations.class)
    private Team team;

    @NotNull
    @JsonProperty("is_leader")
    @JsonView(Views.Membership.class)
    private Boolean isLeader;

    @ElementCollection
    @JsonProperty("user_indicated_timeslot_ids")
	Set<Long> userIndicatedTimeslotIds = new HashSet<>();

    @Column(name = "indicated_availabilities")
    @JsonProperty("indicated_availabilities")
    boolean indicatedAvailabilities = false;

    public TeamMember(User user, Team team, Boolean isLeader) {
        this.user = user;
        this.team = team;
        this.isLeader = isLeader;
    }

    public boolean hasIndicatedAvailabilities() {
        return this.indicatedAvailabilities;
    }
}
