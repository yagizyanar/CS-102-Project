package productivity.timer;
/**
 * @author Elshan Iskandarli
 */
import productivity.database.SessionDAO;
import productivity.tracking.StreakMan;
import productivity.tracking.ProductivityTracker;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class PomodoroSession {
    private int sessionId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int durationMin;
    private int breakMin;
    private boolean isActive;
    private boolean isGroupSession;
    private Timer timer;

    public PomodoroSession(int durationMin, int breakMin) {
        this.durationMin = durationMin;
        this.breakMin = breakMin;
        this.isActive = false;
        this.isGroupSession = false;
    }

    /**
     * Sets the start time to now and starts countdown thread
     */
    public void startTimer() {
        this.startTime = LocalDateTime.now();
        this.isActive = true;
        System.out.println("Pmodoro timer has started. Session is " + durationMin + " minutes long.");

        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            public void run() {
                endSession();
                startBreak();
            }
        }, durationMin * 60 * 1000L);
    }

    /**
     * Ends the study session and records it
     */
    public void endSession() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        
        this.endTime = LocalDateTime.now();
        this.isActive = false;
        int minutes = calculateStdTime();
        System.out.println("Session in over. You studies for " + minutes + " minutes.");
        SessionDAO.saveStudySession(this);

        StreakMan.getInstance().recordStdDay(minutes);

        int xp = calculateXP(minutes);
        ProductivityTracker.addXP(xp);

        System.out.println("Your earned " + xp + " XP.");
    }

    /**
     * Calculates the study session duration by subtracting start time from current time
     */
    public int calculateStdTime() {
        if(startTime == null || endTime == null) {
            return 0;
        }
        int studyTime = (int)(Duration.between(startTime, endTime).toMinutes());
        return studyTime;
    }

    private void startBreak() {
        if(breakMin <= 0) {
            return;
        }

        Timer breakTime = new Timer();
        breakTime.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Break time is over. Next session is starting.");
                breakTime.cancel();

            }
        }, breakMin * 60 * 1000L);
    }

    public void pauseTimer() {
        if(!isActive || timer == null) {
            return;
        }
        
        timer.cancel();
        timer = null;
        isActive = false;
        System.out.println("Timer pauswd.");
        
    }

    public int calculateXP(int minutes) {
        int xp = 0;

        if(isGroupSession) {
            xp = 5 + minutes / 5 + 10;
        } else {
            xp = 5 + minutes / 5;
        }

        return xp;
    }

    public String getRemainingTime() {
        if(!isActive && startTime == null) {
            return "00:00";
        }

        String rem = "";

        LocalDateTime now = LocalDateTime.now();
        Duration past = Duration.between(startTime, now);
        Duration remaining = Duration.ofMinutes(durationMin).minus(past);

        long mins = remaining.toMinutes();
        if(mins < 10) {
            rem += "0" + mins;
        } else {
            rem += mins;
        }
        long secs = remaining.minusMinutes(mins).getSeconds();
        if(mins < 10) {
            rem += "0" + secs;
        } else {
            rem += secs;
        }

        return rem;
    }

    public int getSessionId() {
        return sessionId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getDurationMin() {
        return durationMin;
    }

    public int getBreakMin() {
        return breakMin;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public boolean isIsGroupSession() {
        return isGroupSession;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setDurationMin(int durationMin) {
        this.durationMin = durationMin;
    }

    public void setBreakMin(int breakMin) {
        this.breakMin = breakMin;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setIsGroupSession(boolean isGroupSession) {
        this.isGroupSession = isGroupSession;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

}
