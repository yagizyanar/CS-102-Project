package productivity.task;
/**
 * @author Elshan Iskandarli
 */
import java.time.LocalDateTime;

public class Goal extends Task {
    private boolean isLongTerm;
    private String category;

    public Goal(String title, String description, LocalDateTime targetDate, boolean isLongTerm, String category) {
        super(title, description, targetDate);
        this.isLongTerm = isLongTerm;
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
}
