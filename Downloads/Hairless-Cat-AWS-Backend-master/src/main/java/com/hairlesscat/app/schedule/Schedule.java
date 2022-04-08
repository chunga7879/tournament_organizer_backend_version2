package com.hairlesscat.app.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.hairlesscat.app.match.Match;
import com.hairlesscat.app.tournament.Tournament;
import com.hairlesscat.app.tournamenttimeslot.TournamentTimeslot;
import com.hairlesscat.app.view.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schedule")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class Schedule {
    @Id
    @SequenceGenerator(
            name = "schedule_sequence_generator",
            sequenceName = "schedule_sequence_generator",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "schedule_sequence_generator",
            strategy = GenerationType.SEQUENCE
    )
    @JsonProperty("schedule_id")
    @Column(name = "schedule_id")
    @JsonView(Views.Public.class)
    private Long scheduleId;

    @JsonProperty("tournament_start_time")
    @NotNull
    @Column(
            columnDefinition = "timestamp"
    )
    @JsonView(Views.ScheduleSummary.class)
    private LocalDateTime tournamentStartTime;

    @JsonProperty("tournament_end_time")
    @NotNull
    @Column(
            columnDefinition = "timestamp"
    )
    @JsonView(Views.ScheduleSummary.class)
    private LocalDateTime tournamentEndTime;

    @OneToOne(mappedBy = "schedule")
    @JsonIgnore
    private Tournament tournament;

    @OneToMany(
            mappedBy = "schedule",
            cascade = CascadeType.ALL
    )
    @JsonProperty("timeslots")
    @JsonView(Views.ScheduleFull.class)
    private List<TournamentTimeslot> timeslots;

    @OneToMany(
            mappedBy = "schedule",
            cascade = CascadeType.ALL)
    @JsonIgnore()
    List<Match> matches;

    @Column(name = "schedule_status")
    @Builder.Default
    @JsonProperty("schedule_status")
    @JsonView(Views.ScheduleSummary.class)
    private ScheduleStatus scheduleStatus = ScheduleStatus.SCHEDULE_NOT_GENERATED;

    @Column(name = "schedule_status_error_message")
    @JsonProperty("schedule_status_error_message")
    @JsonView(Views.ScheduleExtended.class)
    private String scheduleStatusErrorMessage;

    public void setScheduleStatusError(String message) {
        this.scheduleStatus = ScheduleStatus.SCHEDULE_ERROR;
        this.scheduleStatusErrorMessage = message;
    }

    public void setScheduleStatusSuccess() {
        this.scheduleStatus = ScheduleStatus.SCHEDULE_GENERATED_SUCCESS;
    }

    public boolean hasScheduleError() {
        return scheduleStatus == ScheduleStatus.SCHEDULE_ERROR;
    }

    public boolean isScheduled() {
        return scheduleStatus == ScheduleStatus.SCHEDULE_GENERATED_SUCCESS;
    }

    public boolean hasNotStartedScheduling() {
        return scheduleStatus == ScheduleStatus.SCHEDULE_NOT_GENERATED;
    }
}
