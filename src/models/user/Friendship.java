/**
 * @author Javanshir Aghayev
 */

package models.user;

import java.time.LocalDateTime;

public class Friendship {
    public enum FriendshipStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        BLOCKED
    }

    // Attributes
    private int friendshipId;
    private int senderId;
    private int receiverId;
    private FriendshipStatus status;
    private LocalDateTime requestDate;
    private LocalDateTime responseDate;

    public Friendship(int friendshipId, int senderId, int receiverId) {
        this.friendshipId = friendshipId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = FriendshipStatus.PENDING;
        this.requestDate = LocalDateTime.now();
        this.responseDate = null;
    }

    public Friendship(int friendshipId, int senderId, int receiverId,
                      FriendshipStatus status, LocalDateTime requestDate, LocalDateTime responseDate) {
        this.friendshipId = friendshipId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
        this.requestDate = requestDate;
        this.responseDate = responseDate;
    }

    public static Friendship sendRequest(int senderId, int receiverId) {
        if (senderId == receiverId) {
            System.out.println("Cannot send friend request to yourself");
            return null;
        }

        int newFriendshipId = generateFriendshipId();
        Friendship friendship = new Friendship(newFriendshipId, senderId, receiverId);

        System.out.println("Friend request sent from user " + senderId + " to user " + receiverId);

        return friendship;
    }

    public boolean acceptRequest(int userId) {
        if (userId != this.receiverId) {
            System.out.println("Only the receiver can accept the friend request");
            return false;
        }

        if (this.status != FriendshipStatus.PENDING) {
            System.out.println("Friend request is not pending. Current status: " + this.status);
            return false;
        }

        this.status = FriendshipStatus.ACCEPTED;
        this.responseDate = LocalDateTime.now();

        System.out.println("Friend request accepted. Users " + senderId + " and " + receiverId + " are now friends");

        return true;
    }

    public boolean rejectRequest(int userId) {
        if (userId != this.receiverId) {
            System.out.println("Only the receiver can reject the friend request");
            return false;
        }

        if (this.status != FriendshipStatus.PENDING) {
            System.out.println("Friend request is not pending. Current status: " + this.status);
            return false;
        }

        this.status = FriendshipStatus.REJECTED;
        this.responseDate = LocalDateTime.now();

        System.out.println("Friend request rejected by user " + receiverId);

        return true;
    }

    public static boolean removeFriend(int userId1, int userId2) {
        System.out.println("Friendship removed between user " + userId1 + " and user " + userId2);
        return true;
    }

    private static int generateFriendshipId() {
        return (int) (Math.random() * 100000);
    }

    // Getters and Setters
    public int getFriendshipId() {
        return friendshipId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "friendshipId=" + friendshipId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", status=" + status +
                ", requestDate=" + requestDate +
                ", responseDate=" + responseDate +
                '}';
    }
}