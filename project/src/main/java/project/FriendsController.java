package project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class FriendsController {

    @FXML
    private VBox friendsContainer;

    @FXML
    private VBox groupContainer;

    @FXML
    private Button addFriendButton;

    private VBox groupMembersContainer;
    private Label capacityLabel;
    private Label groupNameLabel;
    private Button leaveGroupButton;

    // ========== DATA STORAGE (static - simulates database) ==========
    
    private static HashMap<String, User> allUsers = new HashMap<>();
    
    private static ArrayList<User> friends = new ArrayList<>();
    
    private static HashMap<String, Group> allGroups = new HashMap<>();
    
    private static Group currentGroup = null;
    
    private static User currentUser = null;
    
    private static final int GROUP_CAPACITY = 10;

    
    public static class User {
        String username;
        String profileImage;
        int level;

        public User(String username, String profileImage, int level) {
            this.username = username;
            this.profileImage = profileImage;
            this.level = level;
        }
        
        public String getUsername() { return username; }
        public String getProfileImage() { return profileImage; }
        public int getLevel() { return level; }
    }

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
            if (members.size() >= GROUP_CAPACITY) {
                return false;
            }
            if (!members.contains(user)) {
                members.add(user);
                return true;
            }
            return false;
        }

        public void removeMember(User user) {
            members.remove(user);
        }

        public int getMemberCount() {
            return members.size();
        }

        public String getCapacityText() {
            return members.size() + "/" + GROUP_CAPACITY;
        }
        
        public String getGroupName() { return groupName; }
        public ArrayList<User> getMembers() { return members; }
        public User getOwner() { return owner; }
    }

    
    static {
        initializeSampleData();
    }
    
    private static void initializeSampleData() {
        // Şu anki kullanıcı
        currentUser = new User("CurrentUser", null, 15);
        
        // Örnek kullanıcılar (database'den gelecek)
        allUsers.put("Ali", new User("Ali", null, 11));
        allUsers.put("Elshan", new User("Elshan", null, 11));
        allUsers.put("Serhat", new User("Serhat", null, 16));
        allUsers.put("Yagiz", new User("Yagiz", null, 15));
        allUsers.put("Cinar", new User("Cinar", null, 15));
        allUsers.put("Javanshir", new User("Javanshir", null, 11));
    }

    @FXML
    private void initialize() {
        refreshFriendsList();
        refreshGroupView();
    }


    @FXML
    private void showAddFriendDialog(ActionEvent e) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add a Friend");
        dialog.setHeaderText("Enter username to add as friend");
        dialog.setContentText("Username:");
        dialog.getDialogPane().setStyle("-fx-background-color: #E8E0F0;");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            User user = findUserByUsername(username);
            
            if (user == null) {
                showError("User Not Found", "No user found with username: " + username);
            } else if (friends.contains(user)) {
                showError("Already Friends", "You are already friends with " + username);
            } else {
                friends.add(user);
                refreshFriendsList();
                showInfo("Success", username + " added to your friends list!");
            }
        });
    }

    private void refreshFriendsList() {
    if (friendsContainer == null) return;
    
    Node friendsLabel = friendsContainer.getChildren().get(0);
    Node addButton = friendsContainer.getChildren().get(friendsContainer.getChildren().size() - 1);
    
    friendsContainer.getChildren().clear();
    
    friendsContainer.getChildren().add(friendsLabel);
    
    for (User friend : friends) {
        HBox friendRow = createFriendRow(friend);
        friendsContainer.getChildren().add(friendRow);
    }
    
    friendsContainer.getChildren().add(addButton);
}

    private HBox createFriendRow(User user) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefHeight(80);
        row.setPadding(new Insets(10));

        Circle profileCircle = new Circle(30);
        profileCircle.setFill(Color.web("#E0E0E0"));
        
        if (user.profileImage != null && !user.profileImage.isEmpty()) {
            try {
                Image img = new Image(getClass().getResourceAsStream(user.profileImage));
                profileCircle.setFill(new ImagePattern(img));
            } catch (Exception e) {
            }
        }

        VBox infoBox = new VBox(2);
        
        Label nameLabel = new Label(user.username);
        nameLabel.setFont(Font.font("System Bold", 16));
        
        Label levelLabel = new Label("lvl." + user.level);
        levelLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12;");
        
        infoBox.getChildren().addAll(nameLabel, levelLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button removeBtn = new Button("X");
        removeBtn.setStyle("-fx-background-color: #c6131b; -fx-text-fill: white; -fx-font-weight: bold;");
        removeBtn.setOnAction(e -> {
            friends.remove(user);
            refreshFriendsList();
        });

        row.getChildren().addAll(profileCircle, infoBox, spacer, removeBtn);
        return row;
    }


    @FXML
    private void showCreateGroupDialog(ActionEvent e) {
        if (currentGroup != null) {
            showError("Already in Group", "You must leave your current group before creating a new one.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create a Group");
        dialog.setHeaderText("Enter a name for your group");
        dialog.setContentText("Group Name:");
        dialog.getDialogPane().setStyle("-fx-background-color: #E8E0F0;");

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
            showError("Already in Group", "You must leave your current group before joining another.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Join a Group");
        dialog.setHeaderText("Enter the group name to join");
        dialog.setContentText("Group Name:");
        dialog.getDialogPane().setStyle("-fx-background-color: #E8E0F0;");

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
        
        groupContainer.getChildren().removeIf(node -> 
            node == groupMembersContainer || 
            node == capacityLabel || 
            node == groupNameLabel ||
            node == leaveGroupButton
        );
        
        if (currentGroup != null) {
            
            groupNameLabel = new Label(currentGroup.groupName);
            groupNameLabel.setFont(Font.font("System Bold", 20));
            groupNameLabel.setStyle("-fx-text-fill: #59B5E0;");
            VBox.setMargin(groupNameLabel, new Insets(10, 0, 0, 0));
            
            capacityLabel = new Label(currentGroup.getCapacityText());
            capacityLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 14;");
            VBox.setMargin(capacityLabel, new Insets(5, 0, 10, 0));
            
            groupMembersContainer = new VBox(5);
            groupMembersContainer.setPadding(new Insets(10));
            
            for (User member : currentGroup.members) {
                HBox memberRow = createGroupMemberRow(member);
                groupMembersContainer.getChildren().add(memberRow);
            }
            

            leaveGroupButton = new Button("Leave Group");
            leaveGroupButton.setStyle("-fx-background-color: #c6131b; -fx-text-fill: white;");
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
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefHeight(60);
        row.setPadding(new Insets(5, 10, 5, 10));

        Circle profileCircle = new Circle(25);
        profileCircle.setFill(Color.web("#E0E0E0"));
        
        if (user.profileImage != null && !user.profileImage.isEmpty()) {
            try {
                Image img = new Image(getClass().getResourceAsStream(user.profileImage));
                profileCircle.setFill(new ImagePattern(img));
            } catch (Exception e) {
            }
        }

        VBox infoBox = new VBox(2);
        
        Label nameLabel = new Label(user.username);
        nameLabel.setFont(Font.font("System Bold", 14));
        
        Label levelLabel = new Label("lvl." + user.level);
        levelLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 11;");
        
        infoBox.getChildren().addAll(nameLabel, levelLabel);

        row.getChildren().addAll(profileCircle, infoBox);
        
        // Owner badge
        if (currentGroup != null && currentGroup.owner == user) {
            Label ownerBadge = new Label("👑");
            ownerBadge.setStyle("-fx-font-size: 16;");
            row.getChildren().add(ownerBadge);
        }

        return row;
    }

    // ========== DATABASE HELPER METHODS ==========
    
    /**
     * DATABASE ENTEGRASYONU: Bu metod database'den kullanıcı arayacak
     */
    private User findUserByUsername(String username) {
        return allUsers.get(username);
    }
    
    /**
     * DATABASE ENTEGRASYONU: Bu metod database'den grup arayacak
     */
    private Group findGroupByName(String groupName) {
        return allGroups.get(groupName);
    }
    
    /**
     * DATABASE ENTEGRASYONU: Arkadaş listesini database'den yükle
     */
    public static void loadFriendsFromDatabase() {
        // TODO: Database'den arkadaş listesini çek
        // friends = database.getFriends(currentUser.username);
    }
    
    /**
     * DATABASE ENTEGRASYONU: Arkadaşı database'e kaydet
     */
    public static void saveFriendToDatabase(User friend) {
        // TODO: Database'e arkadaş ekle
        // database.addFriend(currentUser.username, friend.username);
    }
    
    /**
     * DATABASE ENTEGRASYONU: Grubu database'den yükle
     */
    public static void loadGroupFromDatabase() {
        // TODO: Kullanıcının grubunu database'den çek
        // currentGroup = database.getUserGroup(currentUser.username);
    }
    
    /**
     * DATABASE ENTEGRASYONU: Grubu database'e kaydet
     */
    public static void saveGroupToDatabase(Group group) {
        // TODO: Group database
        // database.saveGroup(group);
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


    private void switchPage(ActionEvent e, String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/project/" + fxml));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setWidth(width);
            stage.setHeight(height);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}