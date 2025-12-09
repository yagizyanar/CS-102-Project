package src.pomodoro; 

import java.time.LocalDateTime;
import javafx.animation.AnimationTimer;

public class PomodoroSession {

    private int sessionId;
    private int userId; 
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int durationMinutes; 
    private SessionType sessionType;
    private StudyGroup studyGroup; 

    private Time studyTime;
    private Time breakTime;
    private Time currentTimeLeft;
    private PomodoroStatus currentStatus = PomodoroStatus.STOPPED;
    private User activeUser; 

    private PomodoroAnimator animator;
    private long lastTime = 0;
    
    private Runnable uiUpdater; 

    public enum SessionType { 
        INDIVIDUAL, GROUP; 
    }

    public enum PomodoroStatus { 
        STUDYING, BREAK, STOPPED, PAUSED; 
    }

    public PomodoroSession(int sessionId, User activeUser, SessionType sessionType, Time studyTime, Time breakTime, StudyGroup studyGroup, Runnable uiUpdater) {
        this.sessionId = sessionId;
        this.activeUser = activeUser;
        this.userId = activeUser.getId(); 
        this.sessionType = sessionType;
        this.studyTime = studyTime;
        this.breakTime = breakTime;
        this.currentTimeLeft = studyTime;
        this.studyGroup = studyGroup; 
        this.durationMinutes = 0;
        
        this.uiUpdater = uiUpdater; 
        this.animator = new PomodoroAnimator();
    }
    
    private class PomodoroAnimator extends AnimationTimer {
        
        @Override
        public void handle(long now) {
            if (lastTime == 0) {
                lastTime = now;
                return;
            }
            
            if (now > lastTime + 1_000_000_000) {                
                if (currentTimeLeft.getTotalSeconds() > 0) {
                    int currentSeconds = currentTimeLeft.getTotalSeconds();
                    currentSeconds--;
                    
                    int newTotalSeconds = currentSeconds;
                    
                    int hours = newTotalSeconds / 3600;
                    newTotalSeconds %= 3600;
                    int minutes = newTotalSeconds / 60;
                    int seconds = newTotalSeconds % 60;
                    
                    currentTimeLeft = new Time(hours, minutes, seconds); 
                    
                    if (uiUpdater != null) {
                        uiUpdater.run(); 
                    }
                    
                    lastTime = now;
                    
                } else {
                    this.stop(); 
                    lastTime = 0;
                    changePomodoroStatus();
                }
            }
        }

        @Override
        public void start() {
            super.start();
            lastTime = 0;
        }
    }
    
    public void startTimer(int duration, SessionType type) {
        if (this.currentStatus != PomodoroStatus.STOPPED && this.currentStatus != PomodoroStatus.PAUSED) {
            return;
        }
        
        if (type == SessionType.GROUP) {
            if (studyGroup == null || !studyGroup.getMembers().contains(activeUser)){
                return;
            }
            if (!studyGroup.checkIsAllReady()) { 
                return;
            }
        }
        
        this.startTime = LocalDateTime.now();
        timeCycle(); 
    }
    
    public void endSession() {
        if (currentStatus != PomodoroStatus.STOPPED) {
            stopTimer(); 
        }
        this.endTime = LocalDateTime.now();
        calculateStudyTime();
        calculateGainedXP();
    }
    
    public int calculateStudyTime() {
        LocalDateTime finishTime;
        
        if (this.endTime != null) {
            finishTime = this.endTime;
        } 
        else {
            finishTime = LocalDateTime.now();
        }
        
        if (this.startTime == null) {
            return 0; 
        }

        java.time.Duration duration = java.time.Duration.between(this.startTime, finishTime); 
        long totalMinutes = duration.toMinutes();
        this.durationMinutes = (int)totalMinutes;

        return this.durationMinutes; 
    }

    public boolean joinGroup(int groupId) {
        if (sessionType == SessionType.GROUP && studyGroup != null) {
            this.studyGroup.addMember(this.activeUser); 
            return true; 
        }
        return false;
    }
    
    public void timeCycle() { 
        this.currentStatus = PomodoroStatus.STUDYING;
        this.currentTimeLeft = studyTime;
        animator.start(); 
    }
    
    public void pauseTimer() {
        if (currentStatus == PomodoroStatus.STUDYING || currentStatus == PomodoroStatus.BREAK) {
            animator.stop();
            currentStatus = PomodoroStatus.PAUSED;
        }
    }
    
    public void stopTimer() {
        animator.stop(); 
        currentStatus = PomodoroStatus.STOPPED;
        currentTimeLeft = studyTime; 
    }

    public void changePomodoroStatus() {
        if (currentStatus == PomodoroStatus.STUDYING) {
            currentStatus = PomodoroStatus.BREAK;
            currentTimeLeft = breakTime;
            animator.start(); 
        } 
        else if (currentStatus == PomodoroStatus.BREAK) {
            stopTimer();
            endSession(); 
        }
    }

    public void skipBreakAndRestart() {
        if (currentStatus == PomodoroStatus.BREAK) {
            animator.stop(); 
            currentStatus = PomodoroStatus.STUDYING; 
            currentTimeLeft = studyTime;
            animator.start(); 
        }
    }

    public void calculateGainedXP() {         
        int xpPerMinute = 10; 
        int gainedXP = this.durationMinutes * xpPerMinute;
        
        if (this.sessionType == SessionType.GROUP && this.studyGroup != null) {
            for(int i = 0; i < studyGroup.getReadyMembers().size(); i++){
                studyGroup.getReadyMembers().get(i).updateXP(gainedXP);
            }
        }
    }

    public Time getCurrentTimeLeft(){ 
        return this.currentTimeLeft; 
    }

    public PomodoroStatus getCurrentStatus() { 
        return this.currentStatus; 
    }

    public Time getStudyTime(){ 
        return this.studyTime; 
    }

    public Time getBreakTime(){ 
        return this.breakTime; 
    }

    public int getSessionId() { 
        return this.sessionId; 
    }

    public User getActiveUser() {
        return this.activeUser;
    }
}