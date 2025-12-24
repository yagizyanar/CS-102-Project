package com.edutrack.util;

import java.util.List;

import com.edutrack.dao.BadgeDAO;
import com.edutrack.dao.FriendDAO;
import com.edutrack.dao.TaskDAO;
import com.edutrack.model.Badge;
import com.edutrack.model.Task;
import com.edutrack.model.User;

public class BadgeService {

    private static final BadgeDAO badgeDAO = new BadgeDAO();
    private static final TaskDAO taskDAO = new TaskDAO();
    private static final FriendDAO friendDAO = new FriendDAO();

    // Initialize badge tables
    public static void initialize() {
        badgeDAO.initializeTables();
    }

    // Check and award badges for a user
    public static void checkAndAwardBadges(User user) {
        if (user == null) return;

        List<Badge> allBadges = badgeDAO.getAllBadges();
        int userId = user.getId();

        for (Badge badge : allBadges) {
            // Skip if user already has this badge
            if (badgeDAO.userHasBadge(userId, badge.getBadgeId())) {
                continue;
            }

            boolean shouldAward = false;

            switch (badge.getCategory()) {
                case STREAK:
                    shouldAward = user.calculateStreak() >= badge.getRequiredValue();
                    break;
                case XP:
                    shouldAward = user.getXpAmount() >= badge.getRequiredValue();
                    break;
                case PRODUCTIVITY:
                    shouldAward = checkProductivityBadge(user, badge);
                    break;
                case SOCIAL:
                    shouldAward = checkSocialBadge(user, badge);
                    break;
                default:
                    break;
            }

            if (shouldAward) {
                badgeDAO.awardBadge(userId, badge.getBadgeId());
                user.addBadge(badge);
                System.out.println("Badge awarded: " + badge.getName() + " to " + user.getUsername());
            }
        }
    }

    private static boolean checkProductivityBadge(User user, Badge badge) {
        // Count completed tasks
        List<Task> tasks = taskDAO.getTasksByUserId(user.getId());
        int completedTasks = 0;
        for (Task t : tasks) {
            if ("COMPLETED".equals(t.getStatus())) {
                completedTasks++;
            }
        }
        return completedTasks >= badge.getRequiredValue();
    }

    private static boolean checkSocialBadge(User user, Badge badge) {
        // Count friends
        int friendCount = friendDAO.getFriends(user.getId()).size();
        return friendCount >= badge.getRequiredValue();
    }

    // Get user's earned badges
    public static List<Badge> getUserBadges(int userId) {
        return badgeDAO.getUserBadges(userId);
    }

    // Get all available badges
    public static List<Badge> getAllBadges() {
        return badgeDAO.getAllBadges();
    }

    // Award XP and check for new badges
    public static void addXPAndCheckBadges(User user, int xp) {
        if (user == null) return;
        
        user.setXpAmount(user.getXpAmount() + xp);
        
        // Update in database
        new com.edutrack.dao.UserDAO().addPoints(user.getId(), xp);
        
        // Check for new badges
        checkAndAwardBadges(user);
    }
}
