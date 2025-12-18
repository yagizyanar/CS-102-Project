package models.tracking;

import dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StreakMan {
    private static StreakMan instance;

    public static int getDAILY_GOAL_MINS() {
        return DAILY_GOAL_MINS;
    }
    private int curStreak = 0;
    private int longestStreak = 0;
    private LocalDate lastStdDate;
    private static final int DAILY_GOAL_MINS = 25;

    private StreakMan() {
        loadFromDatabase();
    }

    public static StreakMan getInstance() {
        if(instance == null) {
            instance = new StreakMan();
        }
        return instance;
    }

    public void recordStdDay(int minutes) {
        LocalDate today = LocalDate.now();

        if(minutes >= DAILY_GOAL_MINS) {
            if (lastStdDate != null && lastStdDate.equals(today.minusDays(1))) {
                curStreak++;
                System.out.println("Streak increased to: " + curStreak + " days");
            } else if (lastStdDate != null && lastStdDate.isBefore(today)) {
                curStreak = 1;
                System.out.println("Streak reset. New streak started: 1 day.");
            } else if (lastStdDate == null || !lastStdDate.isEqual(today)) {
                if (curStreak == 0) {
                    curStreak = 1;
                 }
            }

            if (curStreak > longestStreak) {
                longestStreak = curStreak;
            }
            lastStdDate = today;
            saveToDatabase();
        }
    }

    public void checkMissedDay() {
        if(lastStdDate == null) {
            return;
        }

        LocalDate yesterday = LocalDate.now().minusDays(1);
        if(lastStdDate.isBefore(yesterday)) {
            curStreak = 0;
            System.out.println("Streak lost. Current streak is 0");
            saveToDatabase();
        }
    }

    private void loadFromDatabase() {
        String sql = "SELECT cur_streak, longest_streak, last_study_date FROM user_progress WHERE user_id = 1"; 
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                curStreak = rs.getInt("cur_streak");
                longestStreak = rs.getInt("longest_streak");
                
                String lastDateStr = rs.getString("last_study_date"); 
                if (lastDateStr != null) {
                    lastStdDate = LocalDate.parse(lastDateStr);
                }
            }
            checkMissedDay();
            
        } catch (SQLException e) {
            System.err.println("Error loading streak data: " + e.getMessage());
        }
    }

    private void saveToDatabase() {
        String sql = "INSERT INTO user_progress (user_id, cur_streak, longest_streak, last_study_date) " +
                     "VALUES (1, ?, ?, ?) " + 
                     "ON DUPLICATE KEY UPDATE cur_streak=?, longest_streak=?, last_study_date=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String dateStr = lastStdDate != null ? lastStdDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
            
            pstmt.setInt(1, curStreak);
            pstmt.setInt(2, longestStreak);
            pstmt.setString(3, dateStr); 

            pstmt.setInt(4, curStreak);
            pstmt.setInt(5, longestStreak);
            pstmt.setString(6, dateStr); 
            
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving streak data: " + e.getMessage());
        }
    }

    public int getCurStreak() {
        return curStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public LocalDate getLastStdDate() {
        return lastStdDate;
    }
}