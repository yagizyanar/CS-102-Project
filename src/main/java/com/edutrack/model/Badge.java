package com.edutrack.model;

import java.util.HashMap;
import java.util.Map;

public class Badge {
    public enum BadgeCategory {
        STREAK,
        XP,
        SOCIAL,
        COURSE,
        PRODUCTIVITY,
        SPECIAL
    }

    private int badgeId;
    private String name;
    private String description;
    private String iconPath;
    private BadgeCategory category;
    private Map<String, Object> criteria;
    private int requiredValue;

    public Badge(int badgeId, String name, String description, String iconPath,
            BadgeCategory category, int requiredValue) {
        this.badgeId = badgeId;
        this.name = name;
        this.description = description;
        this.iconPath = iconPath;
        this.category = category;
        this.requiredValue = requiredValue;
        this.criteria = new HashMap<>();
    }

    public boolean checkCriteria(User user) {
        switch (category) {
            case STREAK:
                return checkStreakCriteria(user);
            case XP:
                return checkXPCriteria(user);
            case SOCIAL:
                return false;
            case COURSE:
                return false;
            default:
                return false;
        }
    }

    private boolean checkStreakCriteria(User user) {
        return user.calculateStreak() >= requiredValue;
    }

    private boolean checkXPCriteria(User user) {
        return user.getXpAmount() >= requiredValue;
    }

    public boolean award(User user) {
        for (Badge userBadge : user.getBadges()) {
            if (userBadge.getBadgeId() == this.badgeId) {
                System.out.println("User already has badge: " + this.name);
                return false;
            }
        }

        if (!checkCriteria(user)) {
            System.out.println("User does not meet criteria for badge: " + this.name);
            return false;
        }

        user.addBadge(this);

        System.out.println("Badge '" + this.name + "' awarded to user " + user.getUsername());
        return true;
    }

    public static Badge createStreakBadge(String name, String description, int streakDays) {
        int badgeId = generateBadgeId();
        return new Badge(badgeId, name, description,
                "com/edutrack/view/images/badges/streak_" + streakDays + ".png",
                BadgeCategory.STREAK, streakDays);
    }

    public static Badge createXPBadge(String name, String description, int xpAmount) {
        int badgeId = generateBadgeId();
        return new Badge(badgeId, name, description,
                "com/edutrack/view/images/badges/xp_" + xpAmount + ".png",
                BadgeCategory.XP, xpAmount);
    }

    // Helpers
    public void addCriteria(String key, Object value) {
        criteria.put(key, value);
    }

    private static int generateBadgeId() {
        return (int) (Math.random() * 100000);
    }

    // Getters
    public int getBadgeId() {
        return badgeId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIconPath() {
        return iconPath;
    }

    public BadgeCategory getCategory() {
        return category;
    }

    public int getRequiredValue() {
        return requiredValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Badge badge = (Badge) obj;
        return badgeId == badge.badgeId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(badgeId);
    }
}
