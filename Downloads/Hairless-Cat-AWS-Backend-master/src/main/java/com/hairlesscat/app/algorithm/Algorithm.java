package com.hairlesscat.app.algorithm;

import com.hairlesscat.app.match.Match;
import com.hairlesscat.app.princeton.Graph;
import com.hairlesscat.app.princeton.HopcroftKarp;
import com.hairlesscat.app.team.Team;
import com.hairlesscat.app.tournamenttimeslot.TournamentTimeslot;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.math3.util.CombinatoricsUtils.binomialCoefficient;

public class Algorithm {
    /**
     * Generate round robin matches based on Hopcroft-Karp algorithm.
     * Hopcroft-Karp uses a bipartite graph and aims to find the maximal cardinality matching.
     * <p>
     * Every pair of teams will be a vertex on the left side of the bipartite graph.
     * Every timeslot will be a vertex on the right side of the bipartite graph.
     *
     * @param tournamentTimeslots list of timeslots.
     * @param teams list of teams.
     * @return a list of matches generated according to the scheduling algorithm.
     * @throws ImperfectMatchingException if the scheduling algorithm could not find a match time for a particular pair of teams.
     * @throws MoreMatchesThanAvailableTimeslotsException if number of matches required exceeds the number of timeslots provided.
     */
    public static List<Match> genRRMatches(List<TournamentTimeslot> tournamentTimeslots, List<Team> teams) throws ImperfectMatchingException, MoreMatchesThanAvailableTimeslotsException {
        int numTeams = teams.size();
        int numTimeslots = tournamentTimeslots.size();
        int totalNumPairings = (int) (binomialCoefficient(numTeams, 2));

        if (totalNumPairings > numTimeslots) {
            throw new MoreMatchesThanAvailableTimeslotsException(totalNumPairings, numTimeslots);
        }

        int V = totalNumPairings + numTimeslots;

        Graph graph = new Graph(V);

        // Initialise a counter that acts as an index for the paired team vertices
        int pairingsCounter = 0;

        // Double for loop to get all n choose 2 team pairings
        for (int i = 0; i < numTeams; i++) {
            for (int j = i+1; j < numTeams; j++) {
                Team t1 = teams.get(i);
                Team t2 = teams.get(j);
                for (int k = 0; k < numTimeslots; k++) {
                    TournamentTimeslot timeslot = tournamentTimeslots.get(k);
                    // if both teams have agreed to play at this time, then there should be an edge in the bp graph
                    if (timeslot.hasTeam(t1) && timeslot.hasTeam(t2)) {
                        // we add totalNumPairings to k because each timeslot is represented by these vertices indexed starting from totalNumPairings
                        graph.addEdge(pairingsCounter, k + totalNumPairings);
                    }
                }
                pairingsCounter++;
            }
        }

        // Ensure that we didn't mess up our counting
        assert pairingsCounter == totalNumPairings;

        // Hopcroft-Karp Algo
        HopcroftKarp hopcroftKarp = new HopcroftKarp(graph);

        // Create matches based on the matching
        pairingsCounter = 0;
        List<Match> matches = new ArrayList<>();

        for (int i = 0; i < numTeams; i++) {
            for (int j = i+1; j < numTeams; j++) {
                int matchedTimeslotIndex = hopcroftKarp.mate(pairingsCounter);
                if (matchedTimeslotIndex == -1) {
                    throw new ImperfectMatchingException("One or more matches are not scheduled a timeslot.");
                }

                // Must use matchedTimeslotIndex - totalNumPairings because the index returned
                // from the hopcroftKarp algo is includes the team pairings in the count
                TournamentTimeslot matchedTimeslot = tournamentTimeslots.get(matchedTimeslotIndex - totalNumPairings);
                Match match = new Match(List.of(teams.get(i), teams.get(j)), matchedTimeslot.getStartTime(), matchedTimeslot.getEndTime());
                matches.add(match);
                pairingsCounter++;
            }
        }

        // Ensure that we didn't mess up our counting
        assert pairingsCounter == totalNumPairings;

        return matches;
    }
}
