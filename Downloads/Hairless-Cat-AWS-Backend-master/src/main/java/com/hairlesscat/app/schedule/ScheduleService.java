package com.hairlesscat.app.schedule;

import com.hairlesscat.app.algorithm.Algorithm;
import com.hairlesscat.app.algorithm.ImperfectMatchingException;
import com.hairlesscat.app.algorithm.MoreMatchesThanAvailableTimeslotsException;
import com.hairlesscat.app.match.Match;
import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.tournamenttimeslot.TournamentTimeslot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public void addTimeslotsToSchedule(Schedule schedule, List<TournamentTimeslot> timeslots) {
        // Establish bi-directional relationship
        for (TournamentTimeslot t : timeslots) {
            t.setSchedule(schedule);
        }
        schedule.setTimeslots(timeslots);
    }

    public void addTeamToTimeslots(Schedule schedule, Team team, List<Long> timeslotIds) {
        List<TournamentTimeslot> tournamentTimeslots = schedule.getTimeslots();

        //TODO: optimise this process (we could use a map so that we don't need to do a n^2 loop
        for (Long timeslotId : timeslotIds) {
            for (TournamentTimeslot tournamentTimeslot : tournamentTimeslots) {
                if (Objects.equals(tournamentTimeslot.getTimeslotId(), timeslotId)) {
                    tournamentTimeslot.addTeam(team);
                }
            }
        }

        scheduleRepository.save(schedule);
    }

    public boolean validateTimeslotIds(Schedule schedule, Collection<Long> timeslotIds) {
        for (Long timeslotId : timeslotIds) {
            boolean isValid = false;

            for (TournamentTimeslot tournamentTimeslot : schedule.getTimeslots()) {
                if (Objects.equals(tournamentTimeslot.getTimeslotId(), timeslotId)) {
                    isValid = true;
                    break;
                }
            }

            if (!isValid) return false;
        }
        return true;
    }

    public Schedule addMatchesToSchedule(Schedule schedule, List<Match> matches) {
        for (Match match : matches) {
            schedule.getMatches().add(match);
        }
        return scheduleRepository.save(schedule);
    }

    public List<Match> generateMatchSchedule(Schedule schedule, List<Team> teams) throws ImperfectMatchingException, MoreMatchesThanAvailableTimeslotsException {
        return Algorithm.genRRMatches(schedule.getTimeslots(), teams);
    }

    public void setScheduleError(Schedule schedule, String errorMsg) {
        schedule.setScheduleStatusError(errorMsg);
        scheduleRepository.save(schedule);
    }

    public void setScheduleSuccess(Schedule schedule) {
        schedule.setScheduleStatusSuccess();
        scheduleRepository.save(schedule);
    }

    public List<Match> getMatches(Schedule schedule) throws ScheduleNotStartedException, ScheduleErrorException {
        if (schedule.hasNotStartedScheduling()) {
            throw new ScheduleNotStartedException();
        } else if (schedule.hasScheduleError()) {
            throw new ScheduleErrorException(schedule.getScheduleStatusErrorMessage());
        } else {
            return schedule.getMatches();
        }
    }

    @Transactional
    public void resetTeamAvailabilities(Schedule schedule) {
        List<TournamentTimeslot> timeslots = schedule.getTimeslots();
        timeslots.forEach(timeslot -> timeslot.setAvailableTeams(new ArrayList<>()));
    }
}
