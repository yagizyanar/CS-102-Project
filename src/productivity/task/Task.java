package src.productivity.task;
/**
 * @author Elshan Iskandarli
 */
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private boolean complete;
    private int xpReward;

    public Task(String title, String description, LocalDateTime deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.complete = false;
        this.xpReward = 10;

    }

    public Task(int id, String title, String description, LocalDateTime deadline, boolean completed, int xpReward) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.complete = completed;
        this.xpReward = xpReward;
    }
    
    /**
     * This method sets the task completed and adds xp to the users xp's
     */
    public void markAsComplete() {
        this.complete = true;
        System.out.println("Task completed: " + title + " (+" + xpReward + " XP)");

    }

    /**
     * Checks if the task's overdue by comparing current time to the deadline and checking if it is completed
     * */
    public boolean isOverdue() {
        if(LocalDateTime.now().isAfter(deadline) && !complete) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the deadline for the task to user's input
     */
    public void setDeadline(LocalDateTime newDeadline) {
        this.deadline = newDeadline;
        System.out.println("Deadline has been set to " + newDeadline + " for " + title);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    @Override
    public String toString() {
        String overdueSign = "";
        String isComplete = "";
        if(complete) {
            isComplete = "is COMPLETE";
        } else if(isOverdue()) {
            overdueSign = "(OVERDUE)";
        } else {
            isComplete = "is INCOMPLETE";
        }

        String deadlineStr = deadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return title + " " + isComplete + ", deadline for " + deadlineStr + " " + overdueSign;
    } 
}
