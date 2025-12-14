package productivity.task;

import productivity.database.DatabaseConnection;
import productivity.tracking.ProductivityTracker;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;
    private List<Goal> goals;

    public TaskManager() {
       tasks = new ArrayList<>();
       goals = new ArrayList<>();
       loadFromDatabase(); 
    }

    public void createTask(String title, String description, LocalDateTime deadline) {
        Task task = new Task(title, description, deadline);
        saveTaskToDatabase(task);
        tasks.add(task);
        System.out.println(title+ "has been added to tasks.");
    }

    public void createGoal(String title, String description, LocalDateTime targetDate,boolean isLongTerm, String category) {
        Goal goal = new Goal(title, description, targetDate, isLongTerm, category);
        saveGoalToDatabase(goal);
        goals.add(goal);
        System.out.println(title + "has been added to goals.");
    }

    public void completeTask(int id) {
        for(Task task : tasks) {
            if(task.getId() == id) {
                task.markAsComplete();
                updateTaskInDatabase(task);
                ProductivityTracker.addXP(task.getXpReward());
                return;
            }
        }
        System.out.println("No such task can be found.");
    }

    public List<Task> getOverdueTasks() {
        List<Task> overdues = new ArrayList<>();
        for(Task task : tasks) {
            if(task.isOverdue()) {
                overdues.add(task);
            }
        }

        return overdues;
    }

    public List<Task> todaysTasks() {
        List<Task> forToday = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1).withHour(0).withMinute(0);

        for(Task task : tasks) {
            if(!task.isComplete() && !task.isOverdue() && task.getDeadline().isAfter(now) 
                && task.getDeadline().isBefore(tomorrow)) {
                    forToday.add(task);
            }
        }

        return forToday;
    }

    private void saveTaskToDatabase(Task task) {
        
    }

     private void saveGoalToDatabase(Goal goal) {

        saveTaskToDatabase(goal);
    }

}
