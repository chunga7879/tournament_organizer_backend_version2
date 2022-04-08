package com.hairlesscat.app.tournamenttimeslot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.hairlesscat.app.schedule.Schedule;
import com.hairlesscat.app.view.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class Timeslot {
    @Id
    @SequenceGenerator(
            name = "timeslot_sequence_generator",
            sequenceName = "timeslot_sequence_generator",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "timeslot_sequence_generator",
            strategy = GenerationType.SEQUENCE
    )
    @Column(name = "timeslot_id")
    @JsonView(Views.Public.class)
    private Long timeslotId;

    @JsonProperty("timeslot_start_time")
    @NotNull
    @Column(
            columnDefinition = "timestamp"
    )
    @JsonView(Views.Timeslot.class)
    private LocalDateTime startTime;

    @JsonProperty("timeslot_end_time")
    @NotNull
    @Column(
            columnDefinition = "timestamp"
    )
    @JsonView(Views.Timeslot.class)
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(
            name = "schedule_id",
            referencedColumnName = "schedule_id"
    )
    @JsonIgnore
    private Schedule schedule;

    public Timeslot(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
