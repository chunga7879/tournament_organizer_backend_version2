package com.hairlesscat.app.tournamenttimeslot;

import com.hairlesscat.app.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentTimeslotRepository extends JpaRepository<TournamentTimeslot, Long> {

    List<TournamentTimeslot> findTournamentTimeslotsByAvailableTeamsContaining(Team team);

}
