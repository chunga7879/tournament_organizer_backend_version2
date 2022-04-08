package com.hairlesscat.app.schedule;

public class ScheduleNotStartedException extends Exception {
    public ScheduleNotStartedException() {
        super("Schedule algorithm has not been started.");
    }
}
