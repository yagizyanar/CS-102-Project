package src.user;

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
                return checkSocialCriteria(user);
            case COURSE:
                return checkCourseCriteria(user);
            case PRODUCTIVITY:
                return checkProductivityCriteria(user);
            case SPECIAL:
                return checkSpecialCriteria(user);
            default:
                return false;
        }
    }

    private boolean checkStreakCriteria(User user) {
        return user.getCurrentStreak() >= requiredValue;
    }

    private boolean checkXPCriteria(User user) {
        return user.getXpAmount() >= requiredValue;
    }

    private boolean checkSocialCriteria(User user) {
        return user.getFriendIds().size() >= requiredValue;
    }

    private boolean checkCourseCriteria(User user) {
        return user.getEnrolledCourseIds().size() >= requiredValue;
    }

    private boolean checkProductivityCriteria(User user) {
        return false;
    }

    private boolean checkSpecialCriteria(User user) {
        return false;
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

        Notification.createNotification(
                user.getUserId(),
                Notification.NotificationType.BADGE_EARNED,
                "Badge Earned!",
                "Congratulations! You've earned the '" + this.name + "' badge!",
                this.badgeId
        );

        System.out.println("Badge '" + this.name + "' awarded to user " + user.getUsername());

        return true;
    }

    public static Badge createStreakBadge(String name, String description, int streakDays) {
        int badgeId = generateBadgeId();
        return new Badge(badgeId, name, description,
                "badges/streak_" + streakDays + ".png",
                BadgeCategory.STREAK, streakDays);
    }

    public static Badge createXPBadge(String name, String description, int xpAmount) {
        int badgeId = generateBadgeId();
        return new Badge(badgeId, name, description,
                "badges/xp_" + xpAmount + ".png",
                BadgeCategory.XP, xpAmount);
    }

    public static Badge createSocialBadge(String name, String description, int friendCount) {
        int badgeId = generateBadgeId();
        return new Badge(badgeId, name, description,
                "badges/social_" + friendCount + ".png",
                BadgeCategory.SOCIAL, friendCount);
    }

    public static Badge createCourseBadge(String name, String description, int courseCount) {
        int badgeId = generateBadgeId();
        return new Badge(badgeId, name, description,
                "badges/course_" + courseCount + ".png",
                BadgeCategory.COURSE, courseCount);
    }

    public void addCriteria(String key, Object value) {
        criteria.put(key, value);
    }

    private static int generateBadgeId() {
        return (int) (Math.random() * 100000);
    }

    // Getters and Setters
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

    public Map<String, Object> getCriteria() {
        return new HashMap<>(criteria);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Badge badge = (Badge) obj;
        return badgeId == badge.badgeId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(badgeId);
    }

    @Override
    public String toString() {
        return "Badge{" +
                "badgeId=" + badgeId +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", requiredValue=" + requiredValue +
                '}';
    }
}
