package productivity.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import productivity.database.TaskDAO;
import productivity.task.Goal;
import productivity.tracking.ProductivityTracker;
import src.productivity.task.Task;

public class TaskManager {
    private static List<Task> tasks;
    private static List<Goal> goals;

    public TaskManager() {
       tasks = new ArrayList<>();
       goals = new ArrayList<>();
       TaskDAO.loadFromDatabase(); 
    }

    /**
     * createTask() creates a new general task, saves it to the database, and adds it to the databbase.
     */
    public static void createTask(String title, String description, String deadline) {
        Task task = new Task(title, description, deadline);
        TaskDAO.saveTaskToDatabase(task);
        tasks.add(task);
        System.out.println(title+ "has been added to tasks.");
    }

    /**
     * createGoal() creates a new goal
     */
    public void createGoal(int id, String title, String description, LocalDateTime targetDate, boolean isLongTerm, String category) {
        Goal goal = new Goal(id, title, description, targetDate, isLongTerm, category);
        TaskDAO.saveGoalToDatabase(goal);
        goals.add(goal);
        System.out.println(title + "has been added to goals.");
    }

    /**
     * This method marks the task with given id as complete and rewards user with XP
     */
    public void completeTask(int id) {
        for(Task task : tasks) {
            if(task.getId() == id) {
                task.markAsComplete();
                TaskDAO.updateTaskInDatabase(task);
                ProductivityTracker.addXP(task.getXpReward());
                return;
            }
        }
        System.out.println("No such task found.");
    }

    /**
     * This method returns tasks that are overdue
     */
    public List<Task> getOverdueTasks() {
        List<Task> overdues = new ArrayList<>();
        for(Task task : tasks) {
            if(task.isOverdue()) {
                overdues.add(task);
            }
        }

        return overdues;
    }

    /**
     * This method returns all the incomplete tasks that must be completed today
     */
    public List<Task> todaysTasks() {
        List<Task> forToday = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1).withHour(0).withMinute(0);

        for(Task task : tasks) {
            if(!task.isComplete() && !task.isOverdue() && task.getDeadline().isAfter(now) && task.getDeadline().isBefore(tomorrow)) {
                forToday.add(task);
            }
        }

        return forToday;
    }
}
