package src.pomodoro; 

public class Time {
    private int hours;
    private int minutes;
    private int seconds;

    public Time(int minutes, int seconds) {
        this.hours = minutes / 60;
        this.minutes = minutes % 60;
        this.seconds = seconds;
    }

    public Time(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }
    
    public int getTotalSeconds() {
        return (this.hours * 3600) + (this.minutes * 60) + this.seconds;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    public int getHours(){
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }
    
    public int getSeconds(){
        return seconds;
    }
}
