package models.task;
/**
 * @author Elshan Iskandarli
 */
import java.time.LocalDateTime;

public class Goal extends Task {
    private boolean isLongTerm;
    private final String category;
    private final LocalDateTime date;

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

    public Goal(String title, String description, String targetDateStr, boolean isLongTerm, String category) {
        super(title, description, targetDateStr);
        this.isLongTerm = isLongTerm;
        date = LocalDateTime.parse(targetDateStr);
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