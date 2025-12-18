package models.courses;

import java.time.LocalDateTime;

public class ForumPost {
    private int postId;
    private int courseId;
    private int userId;
    private String content;
    private String hashtag;
    private String attachmentPath;
    private LocalDateTime createdAt;
    
    private String authorName;

    public ForumPost() {
        this.createdAt = LocalDateTime.now();
    }

    public ForumPost(int courseId, int userId, String content) {
        this.courseId = courseId;
        this.userId = userId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public void publish() {
        System.out.println("Publishing post: " + content);
        this.createdAt = LocalDateTime.now();
    }

    public void attachFile(String path) {
        this.attachmentPath = path;
    }

    public boolean isCurrentUsersPost(int currentUserId) {
        return this.userId == currentUserId;
    }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getHashtag() { return hashtag; }
    public void setHashtag(String hashtag) { this.hashtag = hashtag; }

    public String getAttachmentPath() { return attachmentPath; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
}