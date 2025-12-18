package model;

import java.time.LocalDateTime;

public class PomodoroSession {

    private int id;
    private int userId;
    private Integer groupId;
    private SessionType sessionType;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int durationMinutes;

    public enum SessionType {
        INDIVIDUAL,
        GROUP
    }

    public PomodoroSession() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int calculateXP() {
        int xpPerMinute = 10;
        int baseXP = durationMinutes * xpPerMinute;

        if (sessionType == SessionType.GROUP) {
            return baseXP + 50;
        }

        return baseXP;
    }

}
