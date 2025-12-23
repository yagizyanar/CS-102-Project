package com.edutrack.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.edutrack.dao.FriendDAO;
import com.edutrack.dao.UserDAO;
import com.edutrack.model.UserRequest;
import com.edutrack.util.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class FriendsController {

    @FXML private VBox friendsContainer;
    @FXML private VBox groupContainer;
    @FXML private Button addFriendButton;
    @FXML private HBox groupButtonsBox;
    @FXML private Button createGroupBtn;
    @FXML private Button joinGroupBtn;

    private VBox groupMembersContainer;
    private Label capacityLabel;
    private Label groupNameLabel;
    private Button leaveGroupButton;

    private static HashMap<String, User> allUsers = new HashMap<>();
    private static ArrayList<User> friends = new ArrayList<>();
    private static HashMap<String, Group> allGroups = new HashMap<>();
    private static Group currentGroup = null;
    private static User currentUser = null;
    private static final int GROUP_CAPACITY = 10;

    // Inner class for User
    public static class User {
        String username;
        String profileImage;
        int level;
        boolean isReady = false;

        public User(String username, String profileImage, int level) {
            this.username = username;
            this.profileImage = profileImage;
            this.level = level;
        }

        public String getUsername() { return username; }
        public String getProfileImage() { return profileImage; }
        public int getLevel() { return level; }
        public boolean isReady() { return isReady; }
        public void setReady(boolean ready) { this.isReady = ready; }
    }

    // Inner class for Group
    public static class Group {
        String groupName;
        ArrayList<User> members;
        User owner;

        public Group(String groupName, User owner) {
            this.groupName = groupName;
            this.owner = owner;
            this.members = new ArrayList<>();
            this.members.add(owner);
        }

        public boolean addMember(User user) {
            if (members.size() >= GROUP_CAPACITY) return false;
            if (!members.contains(user)) {
                members.add(user);
                return true;
            }
            return false;
        }

        public void removeMember(User user) { members.remove(user); }
        public int getMemberCount() { return members.size(); }
        public String getCapacityText() { return members.size() + "/" + GROUP_CAPACITY; }
        public String getGroupName() { return groupName; }
        public ArrayList<User> getMembers() { return members; }
        public User getOwner() { return owner; }
        
        public int getReadyCount() {
            int count = 0;
            for (User u : members) {
                if (u.isReady) count++;
            }
            return count;
        }
        
        public boolean isHalfReady() {
            return getReadyCount() >= Math.ceil(members.size() / 2.0);
        }
    }

    public static Group getCurrentGroup() {
        return currentGroup;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    private static void initializeSampleData() {
        List<com.edutrack.model.User> dbUsers = new UserDAO().getAllUsers();
        for (com.edutrack.model.User dbUser : dbUsers) {
            String avatar = dbUser.getProfilePicture();
            if (avatar == null || avatar.isEmpty()) {
                avatar = "/com/edutrack/view/avatar1.png";
            }
            allUsers.put(dbUser.getUsername(), new User(dbUser.getUsername(), avatar, dbUser.getLevel()));
        }
    }

    @FXML
    private void initialize() {
        com.edutrack.model.User sessionUser = SessionManager.getCurrentUser();
        if (sessionUser != null) {
            String avatar = sessionUser.getProfilePicture();
            if (avatar == null || avatar.isEmpty()) {
                avatar = "/com/edutrack/view/avatar1.png";
            }
            currentUser = new User(sessionUser.getUsername(), avatar, sessionUser.getLevel());
        }

        if (allUsers.isEmpty()) {
            initializeSampleData();
        }

        loadFriendsFromDB();
        refreshFriendsList();
        refreshGroupView();
    }

    private void loadFriendsFromDB() {
        com.edutrack.model.User sessionUser = SessionManager.getCurrentUser();
        if (sessionUser == null) return;

        FriendDAO friendDAO = new FriendDAO();
        UserDAO userDAO = new UserDAO();
        List<UserRequest> dbFriends = friendDAO.getFriends(sessionUser.getId());

        for (UserRequest fr : dbFriends) {
            User existingUser = findUserByUsername(fr.getUsername());
            if (existingUser != null && !friends.contains(existingUser)) {
                friends.add(existingUser);
            } else if (existingUser == null) {
                com.edutrack.model.User dbUser = userDAO.getUserByUsername(fr.getUsername());
                String profilePic = "/com/edutrack/view/avatar1.png";
                int level = 1;
                if (dbUser != null) {
                    if (dbUser.getProfilePicture() != null && !dbUser.getProfilePicture().isEmpty()) {
                        profilePic = dbUser.getProfilePicture();
                    }
                    level = dbUser.getLevel();
                }
                friends.add(new User(fr.getUsername(), profilePic, level));
            }
        }
    }

    private void refreshFriendsList() {
        if (friendsContainer == null) return;
        
        // Keep only the existing children that aren't friend rows
        friendsContainer.getChildren().clear();

        if (friends.isEmpty()) {
            Label noFriends = new Label("No friends yet. Add some friends!");
            noFriends.setStyle("-fx-text-fill: #888; -fx-font-size: 14;");
            friendsContainer.getChildren().add(noFriends);
        } else {
            for (User friend : friends) {
                friendsContainer.getChildren().add(createFriendRow(friend));
            }
        }
    }

    private HBox createFriendRow(User user) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 12;");

        Circle profileCircle = new Circle(25);
        profileCircle.setFill(Color.web("#E0E0E0"));
        loadProfileImage(profileCircle, user.profileImage);

        VBox infoBox = new VBox(2);
        Label nameLabel = new Label(user.username);
        nameLabel.setFont(Font.font("System Bold", 14));
        Label levelLabel = new Label("Level " + user.level);
        levelLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 11;");
        infoBox.getChildren().addAll(nameLabel, levelLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button removeBtn = new Button("✕");
        removeBtn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-background-radius: 15;");
        removeBtn.setOnAction(e -> {
            friends.remove(user);
            refreshFriendsList();
        });

        row.getChildren().addAll(profileCircle, infoBox, spacer, removeBtn);
        return row;
    }

    private void loadProfileImage(Circle circle, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) return;
        try {
            String path = imagePath.startsWith("/") ? imagePath : "/" + imagePath;
            java.io.InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                circle.setFill(new ImagePattern(new Image(is)));
            }
        } catch (Exception e) { }
    }

    @FXML
    private void showAddFriendDialog(ActionEvent e) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add a Friend");
        dialog.setHeaderText("Enter username to add as friend");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            User user = findUserByUsername(username);

            if (user == null) {
                showError("User Not Found", "No user found with username: " + username);
            } else if (friends.contains(user)) {
                showError("Already Friends", "You are already friends with " + username);
            } else if (currentUser != null && username.equals(currentUser.username)) {
                showError("Error", "You cannot add yourself as a friend.");
            } else {
                com.edutrack.model.User sessionUser = SessionManager.getCurrentUser();
                if (sessionUser != null) {
                    com.edutrack.model.User targetUser = new UserDAO().getUserByUsername(username);
                    if (targetUser != null) {
                        FriendDAO friendDAO = new FriendDAO();
                        friendDAO.sendRequest(sessionUser.getId(), targetUser.getId());
                        friendDAO.acceptRequest(sessionUser.getId(), targetUser.getId());
                    }
                }
                friends.add(user);
                refreshFriendsList();
                showInfo("Success", "You are now friends with " + username + "!");
            }
        });
    }

    @FXML
    private void showCreateGroupDialog(ActionEvent e) {
        if (currentGroup != null) {
            showError("Already in Group", "You must leave your current group first.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create a Group");
        dialog.setHeaderText("Enter a name for your group");
        dialog.setContentText("Group Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(groupName -> {
            if (groupName.trim().isEmpty()) {
                showError("Invalid Name", "Group name cannot be empty.");
                return;
            }

            if (allGroups.containsKey(groupName)) {
                showError("Group Exists", "A group with this name already exists.");
            } else {
                Group newGroup = new Group(groupName, currentUser);
                allGroups.put(groupName, newGroup);
                currentGroup = newGroup;
                refreshGroupView();
                showInfo("Success", "Group '" + groupName + "' created successfully!");
            }
        });
    }

    @FXML
    private void showJoinGroupDialog(ActionEvent e) {
        if (currentGroup != null) {
            showError("Already in Group", "You must leave your current group first.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Join a Group");
        dialog.setHeaderText("Enter the group name to join");
        dialog.setContentText("Group Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(groupName -> {
            Group group = allGroups.get(groupName);

            if (group == null) {
                showError("Group Not Found", "No group found with name: " + groupName);
            } else if (group.getMemberCount() >= GROUP_CAPACITY) {
                showError("Group Full", "This group is full (" + group.getCapacityText() + ")");
            } else {
                if (group.addMember(currentUser)) {
                    currentGroup = group;
                    refreshGroupView();
                    showInfo("Success", "You joined the group '" + groupName + "'!");
                } else {
                    showError("Error", "Could not join the group.");
                }
            }
        });
    }

    private void leaveGroup(ActionEvent e) {
        if (currentGroup == null) return;

        if (currentGroup.owner == currentUser) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Leave Group");
            confirm.setHeaderText("You are the owner of this group");
            confirm.setContentText("Leaving will delete the group. Are you sure?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                allGroups.remove(currentGroup.groupName);
                currentGroup = null;
                refreshGroupView();
            }
        } else {
            currentGroup.removeMember(currentUser);
            currentGroup = null;
            refreshGroupView();
        }
    }

    private void refreshGroupView() {
        if (groupContainer == null) return;

        // Remove dynamic elements
        groupContainer.getChildren().removeIf(node -> 
            node == groupMembersContainer || node == capacityLabel || 
            node == groupNameLabel || node == leaveGroupButton);

        if (currentGroup == null) {
            // Show create/join buttons
            if (groupButtonsBox != null) {
                groupButtonsBox.setVisible(true);
                groupButtonsBox.setManaged(true);
            }
        } else {
            // Hide create/join buttons
            if (groupButtonsBox != null) {
                groupButtonsBox.setVisible(false);
                groupButtonsBox.setManaged(false);
            }

            // Show group info
            groupNameLabel = new Label(currentGroup.groupName);
            groupNameLabel.setFont(Font.font("System Bold", 22));
            groupNameLabel.setStyle("-fx-text-fill: #59B5E0;");
            VBox.setMargin(groupNameLabel, new Insets(10, 0, 0, 0));

            capacityLabel = new Label("Members: " + currentGroup.getCapacityText());
            capacityLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");
            VBox.setMargin(capacityLabel, new Insets(5, 0, 15, 0));

            groupMembersContainer = new VBox(8);
            groupMembersContainer.setPadding(new Insets(10));
            groupMembersContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 12;");

            for (User member : currentGroup.members) {
                groupMembersContainer.getChildren().add(createGroupMemberRow(member));
            }

            leaveGroupButton = new Button("Leave Group");
            leaveGroupButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 20;");
            leaveGroupButton.setOnAction(this::leaveGroup);
            VBox.setMargin(leaveGroupButton, new Insets(20, 0, 0, 0));

            int insertIndex = 1;
            groupContainer.getChildren().add(insertIndex, groupNameLabel);
            groupContainer.getChildren().add(insertIndex + 1, capacityLabel);
            groupContainer.getChildren().add(insertIndex + 2, groupMembersContainer);
            groupContainer.getChildren().add(insertIndex + 3, leaveGroupButton);
        }
    }

    private HBox createGroupMemberRow(User user) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8));

        Circle profileCircle = new Circle(22);
        profileCircle.setFill(Color.web("#E0E0E0"));
        loadProfileImage(profileCircle, user.profileImage);

        VBox infoBox = new VBox(2);
        Label nameLabel = new Label(user.username);
        nameLabel.setFont(Font.font("System Bold", 13));
        Label levelLabel = new Label("Level " + user.level);
        levelLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 10;");
        infoBox.getChildren().addAll(nameLabel, levelLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(profileCircle, infoBox, spacer);

        if (currentGroup != null && currentGroup.owner == user) {
            Label ownerBadge = new Label("👑 Owner");
            ownerBadge.setStyle("-fx-font-size: 12;");
            row.getChildren().add(ownerBadge);
        }

        // Ready indicator
        Circle readyDot = new Circle(6);
        readyDot.setFill(user.isReady ? Color.web("#28a745") : Color.web("#ccc"));
        row.getChildren().add(readyDot);

        return row;
    }

    private User findUserByUsername(String username) {
        return allUsers.get(username);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
