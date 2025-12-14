package src.user;

import java.time.LocalDateTime;

public class Notification {
    public enum NotificationType {
        FRIEND_REQUEST,
        FRIEND_ACCEPTED,
        BADGE_EARNED,
        COURSE_UPDATE,
        FORUM_REPLY,
        GROUP_INVITE,
        STREAK_MILESTONE,
        XP_MILESTONE,
        GENERAL
    }

    private int notificationId;
    private int userId;
    private NotificationType type;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private Integer relatedEntityId;

    public Notification(int notificationId, int userId, NotificationType type,
                        String title, String message) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
        this.readAt = null;
        this.relatedEntityId = null;
    }

    public Notification(int notificationId, int userId, NotificationType type,
                        String title, String message, Integer relatedEntityId) {
        this(notificationId, userId, type, title, message);
        this.relatedEntityId = relatedEntityId;
    }

    public Notification(int notificationId, int userId, NotificationType type,
                        String title, String message, boolean isRead,
                        LocalDateTime createdAt, LocalDateTime readAt, Integer relatedEntityId) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.relatedEntityId = relatedEntityId;
    }

    public static Notification createNotification(int userId, NotificationType type,
                                                  String title, String message) {
        int newNotificationId = generateNotificationId();
        Notification notification = new Notification(newNotificationId, userId, type, title, message);

        System.out.println("Notification created for user " + userId + ": " + title);

        return notification;
    }

    public static Notification createNotification(int userId, NotificationType type,
                                                  String title, String message, Integer relatedEntityId) {
        int newNotificationId = generateNotificationId();
        Notification notification = new Notification(newNotificationId, userId, type,
                title, message, relatedEntityId);

        System.out.println("Notification created for user " + userId + ": " + title);

        return notification;
    }

    public void markAsRead() {
        if (this.isRead) {
            System.out.println("Notification already marked as read");
            return;
        }

        this.isRead = true;
        this.readAt = LocalDateTime.now();

        System.out.println("Notification " + notificationId + " marked as read");
    }

    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;

        System.out.println("Notification " + notificationId + " marked as unread");
    }

    public static int getUnreadCount(int userId) {
        int count = 0;
        System.out.println("User " + userId + " has " + count + " unread notifications");
        return count;
    }

    public boolean delete() {
        System.out.println("Notification " + notificationId + " deleted");
        return true;
    }

    private static int generateNotificationId() {
        return (int) (Math.random() * 100000);
    }

    public int getNotificationId() {
        return notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public Integer getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Integer relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}