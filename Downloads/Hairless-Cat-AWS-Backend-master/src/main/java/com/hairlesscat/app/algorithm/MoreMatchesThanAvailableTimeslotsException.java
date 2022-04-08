package com.hairlesscat.app.algorithm;

public class MoreMatchesThanAvailableTimeslotsException extends Exception {
    public MoreMatchesThanAvailableTimeslotsException(int numOfMatches, int numOfTimeslots) {
        super(String.format("The number of matches required to be generated is " +
                "more than the number of timeslots available: %d matches required" +
                "and %d timeslots available", numOfMatches, numOfTimeslots));
    }
}
