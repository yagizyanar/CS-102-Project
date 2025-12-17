package productivity.timer;

import java.util.Scanner;
import productivity.database.SessionDAO;

public class PomodoroTimer {
    private PomodoroSession curSession;
    private int defaultStdTime = 25;
    private int defaultBrkTime = 5;

    public PomodoroTimer() {
        curSession = new PomodoroSession(defaultStdTime, defaultBrkTime);

    }

    public void startPomodoro() {
        if(curSession.isIsActive()) {
            System.out.println("A session is already active");
            return;
        }

        curSession = new PomodoroSession(defaultStdTime, defaultBrkTime);
        curSession.startTimer();

        new Thread(() -> {
            while(curSession.isIsActive()) {
                System.out.println("Time remaining: " + curSession.getRemainingTime() + " ");
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException e) {
                    break;
                }
            }
            System.out.println("");
        }).start();
    }

    public void startUpdatedSession(int std, int brk) {
        curSession = new PomodoroSession(std, brk);
        curSession.startTimer();
    }

    public void stopSession() {
        if(curSession != null && curSession.isIsActive()) {
            curSession.endSession();
        }
    }

    public void toggle() {
        if(curSession == null) {
            return;
        }

        if(curSession.isIsActive()) {
          
        }
    }

    public void updateSetting(int stdTime, int brkTime) {
        defaultStdTime = stdTime;
        defaultBrkTime = brkTime;

        System.out.println("Study time updated to " + defaultStdTime + " mins, break time updated to " + brkTime + " mins.");

    }

    public void startCli() {
        Scanner in = new Scanner(System.in);

        while (true) { 
            System.out.println("===Pomodoro Timer===");
            System.out.println("1. Start Pomodoro (" + defaultStdTime + "min)");
            System.out.println("2. Custom Pomodoro");
            System.out.println("3. Pause/Resume");
            System.out.println("4. Stop Current");
            System.out.println("5. Settings");
            System.out.println("6. Exit");
            System.out.print("Choose: ");

            int c = in.nextInt();
            in.nextLine();

            if(c == 1) {
                startPomodoro();;
            } else if(c == 2) {
                System.out.print("Update study minutes to ");
                int std = in.nextInt();
                System.out.print("Update break minutes to ");
                int brk = in.nextInt();
                startUpdatedSession(std, brk);
            } else if(c == 3) {

            } else if(c == 4) {
                stopSession();
            } else if(c == 5) {
                System.out.print("Default study time: ");
                    defaultStdTime = in.nextInt();
                    System.out.print("Default break time: ");
                    defaultBrkTime = in.nextInt();
            } else if(c == 6) {
                in.close();
            } else {
                System.out.println("Invalid choice");
            }

        }
    }
}
    
