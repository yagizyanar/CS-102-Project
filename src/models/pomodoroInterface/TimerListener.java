package models.pomodorointerface;

public interface TimerListener {
    void onTick(String formattedTime);
    
    void onFinish();
}
