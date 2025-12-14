package src.user;

public class SimpleDemo {
    public static void main(String[] args) {
        System.out.println("=== EduTrack User & Social System Demo ===\n");

        // 1. USER REGISTRATION
        System.out.println("--- User Registration ---");
        User user1 = User.register("javanshir_dev", "javanshir@edutrack.com", "password123");
        User user2 = User.register("serhat_coder", "serhat@edutrack.com", "pass456");
        User user3 = User.register("elshen_pro", "elshen@edutrack.com", "secure789");
        System.out.println();

        // 2. LOGIN
        System.out.println("--- User Login ---");
        user1.login("password123");
        System.out.println();

        // 3. PROFILE UPDATE
        System.out.println("--- Profile Update ---");
        user1.updateProfile("javanshir_updated", null, "profile_pics/javanshir.jpg");
        System.out.println();

        // 4. COURSE ENROLLMENT
        System.out.println("--- Course Enrollment ---");
        user1.enrollCourse(101); // Java Programming
        user1.enrollCourse(102); // Data Structures
        user1.enrollCourse(101); // Try to enroll again (should fail)
        System.out.println();

        // 5. FRIEND REQUESTS
        System.out.println("--- Friend Requests ---");
        Friendship friendship1 = Friendship.sendRequest(user1.getUserId(), user2.getUserId());
        Friendship friendship2 = Friendship.sendRequest(user1.getUserId(), user3.getUserId());
        System.out.println();

        // 6. ACCEPTING FRIEND REQUESTS
        System.out.println("--- Accepting Friend Requests ---");
        friendship1.acceptRequest(user2.getUserId());

        // Add friends to user lists after acceptance
        user1.addFriend(user2.getUserId());
        user2.addFriend(user1.getUserId());
        System.out.println();

        // 7. REJECTING FRIEND REQUESTS
        System.out.println("--- Rejecting Friend Requests ---");
        friendship2.rejectRequest(user3.getUserId());
        System.out.println();

        // 8. XP SYSTEM
        System.out.println("--- XP System ---");
        user1.updateXPAmount(50);  // Completed a task
        user1.updateXPAmount(100); // Finished a course module
        user1.updateXPAmount(25);  // Participated in forum
        System.out.println("Total XP for " + user1.getUsername() + ": " + user1.getXpAmount());
        System.out.println();

        // 9. STREAK CALCULATION
        System.out.println("--- Streak System ---");
        user1.calculateStreak(); // Same day login
        System.out.println("Current streak: " + user1.getCurrentStreak() + " days");
        System.out.println();

        // 10. NOTIFICATIONS
        System.out.println("--- Notifications ---");
        Notification notif1 = Notification.createNotification(
                user1.getUserId(),
                Notification.NotificationType.FRIEND_ACCEPTED,
                "Friend Request Accepted",
                user2.getUsername() + " accepted your friend request!"
        );

        Notification notif2 = Notification.createNotification(
                user1.getUserId(),
                Notification.NotificationType.XP_MILESTONE,
                "XP Milestone!",
                "Congratulations! You've reached 100 XP!"
        );

        System.out.println("Marking notification as read...");
        notif1.markAsRead();
        System.out.println();

        // 11. BADGES
        System.out.println("--- Badge System ---");

        // Create some badges
        Badge firstFriendBadge = Badge.createSocialBadge(
                "Social Butterfly",
                "Make your first friend",
                1
        );

        Badge xpNoviceBadge = Badge.createXPBadge(
                "XP Novice",
                "Earn your first 100 XP",
                100
        );

        Badge streakStarterBadge = Badge.createStreakBadge(
                "Getting Started",
                "Login for 1 day",
                1
        );

        Badge courseEnthusiastBadge = Badge.createCourseBadge(
                "Course Enthusiast",
                "Enroll in 2 courses",
                2
        );

        // Check and award badges
        firstFriendBadge.award(user1);
        xpNoviceBadge.award(user1);
        streakStarterBadge.award(user1);
        courseEnthusiastBadge.award(user1);

        System.out.println("\nBadges earned by " + user1.getUsername() + ":");
        for (Badge badge : user1.getBadges()) {
            System.out.println("  - " + badge.getName() + ": " + badge.getDescription());
        }
        System.out.println();

        // 12. USER SUMMARY
        System.out.println("--- User Summary ---");
        System.out.println(user1);
        System.out.println("Friends: " + user1.getFriendIds().size());
        System.out.println("Enrolled Courses: " + user1.getEnrolledCourseIds().size());
        System.out.println("Badges: " + user1.getBadges().size());
        System.out.println();

        // 13. REMOVING A FRIEND
        System.out.println("--- Remove Friend ---");
        Friendship.removeFriend(user1.getUserId(), user2.getUserId());
        user1.removeFriend(user2.getUserId());
        System.out.println("Friends after removal: " + user1.getFriendIds().size());
        System.out.println();

        System.out.println("=== Demo Complete ===");
    }
}