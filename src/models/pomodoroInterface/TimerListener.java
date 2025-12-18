package pomodoroInterface;

public interface TimerListener {
    void onTick(String formattedTime);
    
    void onFinish();
}
