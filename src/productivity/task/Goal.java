package productivity.task;
/**
 * @author Elshan Iskandarli
 */
import java.time.LocalDateTime;
import src.productivity.task.Task;

public class Goal extends Task {
    private boolean isLongTerm;
    private String category;
    private LocalDateTime date;

    public Goal(int id, String title, String description, LocalDateTime targetDate, boolean isLongTerm, String category) {
        super(id, title, description, targetDate, false, 10);
        this.isLongTerm = isLongTerm;
        date = targetDate;
        this.category = category;

        if(isLongTerm) {
            setXpReward(50);
        } else {
            setXpReward(20);
        }
    }

    public Goal(String title, String description, LocalDateTime targetDate, boolean isLongTerm, String category) {
        super(title, description, targetDate);
        this.isLongTerm = isLongTerm;
        date = targetDate;
        this.category = category;

        if(isLongTerm) {
            setXpReward(50);
        } else {
            setXpReward(20);
        }
    }

    public boolean isLongTerm() {
        return isLongTerm;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        String str = "GOAL: " + super.toString() + "\nCategory: " + category;
        if(isLongTerm()) {
            str += "\nTerm: Long";
        } else {
            str += "\nTerm: Short";
        }

        return str;
    }

    public LocalDateTime getTargetDate() {
        return date;
    }
}