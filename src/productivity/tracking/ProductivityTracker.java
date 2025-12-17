package productivity.tracking;

import productivity.database.DatabaseConnection;
import productivity.timer.PomodoroSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ProductivityTracker{
    private static int totalMin = 0;
    private static int currentXP = 0;
    private static int currentLevel = 1;
    private static int streak = 0;
    public static ProductivityTracker instance;
    private static LocalDate lastStudyDate = null;
    private static final int[] LEVEL_BARRAGES = {0, 10, 25, 50, 100, 200, 500, 1000 };
    public static final int DAILY_STUDY_GOAL_MINUTES = 25;
    private Map<LocalDate, Integer> dailyStdMins = new HashMap<>();

    private ProductivityTracker() {
        loadFromDatabase();
    }

    public static ProductivityTracker getInstance() {
        if(instance == null) {
            instance = new ProductivityTracker();
        }

        return instance;
    }

    /**
     * Adds duration of the session to the total study duration 
     * and updates streak. then checks for new achievemnt
     */
    public void recordStdSession(PomodoroSession session) {
        int mins = session.calculateStdTime();
        totalMin += mins;
        LocalDate today = LocalDate.now();
        dailyStdMins.merge(today, mins, Integer::sum);
        setLastStudyDate();
        saveSessionToDatabase(mins);
        updateProg();
        

        System.out.println("Study recorded: " + mins + " mins.");
        System.out.println("Total study time: " + totalMin);
    }

    /**
     * Checks the last study date: if it is before yesterday sets the 
     * streak to 0
     */
    public static void missedDayCheck() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        if(lastStudyDate.isBefore(yesterday) && lastStudyDate != null) {
            streak = 0;
        }
    }

    public static void setLastStudyDate() {
        lastStudyDate = LocalDate.now();
    }

    /**
     * Adds xp gained from new session to the current xp and checks 
     * if the user can level up, if yes it calls levelUp method
     */
    public static void addXP(int xp) {
        getInstance().currentXP += xp;
        System.out.println("You gained " + xp + " xps, you have total " + getInstance().currentXP + " xp");
        if(canLevelUp(currentXP, currentLevel)) {
            levelUp();
        }

        getInstance().updateProg();
    }

    private static void updateStreak() {

    }

    /**
     * Checks whether the current xp is enough to jump to a new level
     */
    public static boolean canLevelUp(int currentXP, int currentLevel) {
        if(currentXP >= LEVEL_BARRAGES[currentLevel]) {
            return true;
        } else {
            return false;
        }
    }

    public static int getXPForNextLevel(int currentXP, int currentLevel) {
        if (currentLevel < LEVEL_BARRAGES.length) {
            return LEVEL_BARRAGES[currentLevel] - LEVEL_BARRAGES[currentLevel - 1] - currentXP;
        } else {
            System.out.println("You have reached to max level");
            return 0;

        }
    }

    /**
     * subtracts threshold for the next level from the current xp, so no spare xp is lost
     * for example is the user is at lvl 1 and has 12 xps he jumps to 2nd level and has 12 -10 = 2 xps at hand 
     */
    public static void levelUp() {
        currentXP -= LEVEL_BARRAGES[currentLevel];
        currentLevel++;
        
    }

    public int todaysStdMins() {
        return dailyStdMins.getOrDefault(LocalDate.now(), 0);
    }

    public int getWeekStdMins() {
        int weekTotal = 0;;
        LocalDate today = LocalDate.now();

        for(int i = 0; i< 7; i++) {
            LocalDate date = today.minusDays(i);
            weekTotal += dailyStdMins.getOrDefault(date, 0);
        }

        return weekTotal;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        int x = getXPForNextLevel(currentXP, currentLevel) * 100;
        stats.put("currentLevel", currentLevel);
        stats.put("currentXP", currentXP);
        stats.put("totalStudyMinutes", totalMin);
        stats.put("todayStudyMinutes", todaysStdMins());
        stats.put("weeklyStudyMinutes", getWeekStdMins());
        stats.put("nextLevelXP", getXPForNextLevel(currentXP, currentLevel));
        stats.put("progressPercentage", 
            (int) ((double) currentXP / x));
        
        return stats;
    }

    ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////
    private void saveSessionToDatabase(int minutes) {
        String sql = "INSERT INTO study_sessions(start_time, duration_minutes, xp_earned) " +
                    "VALUES(?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, LocalDateTime.now().toString());
            pstmt.setInt(2, minutes);
            pstmt.setInt(3, minutes / 5); 
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving study session: " + e.getMessage());
        }
    }

    private void updateProg() {
        String sql = "INSERT OR REPLACE INTO user_progress(user_id, current_xp, current_level, " +
                    "total_study_minutes, last_login_date) VALUES(1, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, currentXP);
            pstmt.setInt(2, currentLevel);
            pstmt.setInt(3, totalMin);
            pstmt.setString(4, LocalDate.now().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating progress: " + e.getMessage());
        }
    }

     private void loadFromDatabase() {
        String sql = "SELECT * FROM user_progress WHERE user_id = 1";
        ResultSet rs1;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                currentXP = rs.getInt("current_xp");
                currentLevel = rs.getInt("current_level");
                totalMin = rs.getInt("total_study_minutes");
            }
            
            sql = "SELECT * FROM study_sessions WHERE session_date >= date('now', '-30 days')";
            rs1 = stmt.executeQuery(sql);

            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs1.getString("session_date"));
                int minutes = rs1.getInt("duration_minutes");
                dailyStdMins.merge(date, minutes, Integer::sum);
            }
        } catch (SQLException e) {
            System.err.println("Error loading progress: " + e.getMessage());
        }
    }
    
    public int getCurrentXP() { 
        return currentXP; 
    }
    public int getCurrentLevel() { 
        return currentLevel; 
    }
    public int getTotalStudyMinutes() { 
        return totalMin; 
    }


}