package com.hairlesscat.app.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.hairlesscat.app.match.Match;
import com.hairlesscat.app.teammember.TeamMember;
import com.hairlesscat.app.tournament.Tournament;
import com.hairlesscat.app.view.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity()
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "user_email_unique", columnNames = "email")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @Column(
            name = "user_id",
            columnDefinition = "TEXT",
            nullable = false,
            updatable = false
    )
    @JsonProperty("user_id")
    @JsonView(Views.Public.class)
    private String userId;

    @Column(
            name = "is_admin"
    )
    @NotNull
    @JsonProperty("is_admin")
    @JsonView(Views.UserSummary.class)
    private Boolean isAdmin;

    @Column(
            name = "first_name",
            columnDefinition = "TEXT"
    )
    @NotBlank
    @JsonProperty("f_name")
    @JsonView(Views.UserSummary.class)
    private String fName;

    @Column(
            name = "last_name",
            columnDefinition = "TEXT"
    )
    @NotBlank
    @JsonProperty("l_name")
    @JsonView(Views.UserSummary.class)
    private String lName;

    @Column(
            name = "department",
            columnDefinition = "TEXT"
    )
    @NotBlank
    @JsonView(Views.UserFull.class)
    private String department;

    @Column(
            name = "company",
            columnDefinition = "TEXT"
    )
    @NotBlank
    @JsonView(Views.UserFull.class)
    private String company;

    @Column(
            name = "email",
            columnDefinition = "TEXT"
    )
    @NotBlank
    @Email
    @JsonView(Views.UserSummary.class)
    private String email;

    @Column(
            name = "phone_number",
            columnDefinition = "TEXT"
    )
    @JsonProperty("p_number")
    @JsonView(Views.UserFull.class)
    private String pNumber;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonView(Views.TeamAssociations.class)
    @JsonProperty("team_associations")
    @Builder.Default
    private Set<TeamMember> teamAssociations = new HashSet<>();

    @OneToMany(mappedBy = "adminUser")
    @JsonIgnore
    private List<Tournament> tournaments = new ArrayList<>();

    @OneToMany(mappedBy = "adminUser")
    @JsonIgnore
    private List<Match> matches = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId) && fName.equals(user.fName) && lName.equals(user.lName) && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, fName, lName, email);
    }
}
