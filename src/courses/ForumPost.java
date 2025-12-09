package courses;

import java.time.LocalDateTime;

public class ForumPost {
    private int postId;
    private int courseId;
    private int userId;
    private String content;
    private String hashtag;
    private String attachmentPath;
    private int createdBy;
    private LocalDateTime createdAt;

    public ForumPost(int postId, int courseId, int userId, String content) {
        this.postId = postId;
        this.courseId = courseId;
        this.userId = userId;
        this.content = content;
        this.createdBy = userId;
        this.createdAt = LocalDateTime.now();
    }

    public void publish() {
    }

    public void attachFile(String filePath) {
        this.attachmentPath = filePath;
    }

    public boolean isCurrentUserPost(int currentUserId) {
        if(this.userId == currentUserId){
            return true;//The message belongs to the our user(us)
        }
        else{
            return false;//The message belongs to other users
        }
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}