package models.pomodoro;

public interface TimerListener {
    void onTick(String formattedTime);
    
    void onFinish();
}
