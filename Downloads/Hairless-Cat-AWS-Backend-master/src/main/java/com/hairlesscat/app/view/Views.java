package com.hairlesscat.app.view;

public class Views {
    public interface Public {}
    public interface UserSummary extends Public {}
    public interface TeamSummary extends Public {}
    public interface Membership extends Public {}
    public interface Timeslot extends Public {}
    public interface ScheduleSummary extends Public {}
    public interface MatchSummary extends Public {}
    public interface TournamentParameterSummary extends Public {}
    public interface ResultSummary extends TeamSummary {}
    public interface ResultFull extends ResultSummary, MatchSummary {}
    public interface TeamAssociations extends Membership, TeamSummary {}
    public interface TeamMembers extends Membership, UserSummary {}
    public interface UserFull extends UserSummary, TeamAssociations {}
    public interface TournamentTimeslot extends TeamSummary, Timeslot {}
    public interface TeamExtended extends TeamSummary, TeamMembers {}
    public interface TeamFull extends TeamExtended {}
    public interface MatchFull extends MatchSummary, ResultSummary {}
    public interface ScheduleExtended extends Timeslot, ScheduleSummary, MatchSummary {}
    public interface TournamentSummary extends  ScheduleSummary, TournamentParameterSummary, Public {}
    public interface TournamentParameterFull extends TournamentParameterSummary {}
    public interface TournamentFull extends TeamSummary, ScheduleExtended, TournamentSummary, TournamentParameterFull {}
    public interface ScheduleFull extends TournamentTimeslot, ScheduleExtended {}
}
