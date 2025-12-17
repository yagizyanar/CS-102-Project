package project;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EditInfoController {

    // ✅ Root of the popup/overlay (set fx:id="root" on the top Pane in edit-info fxml)
    @FXML private Pane root;

    @FXML private TextField txtUsername;
    @FXML private TextField txtUniversity;
    @FXML private TextField txtMajor;
    @FXML private ComboBox<String> cmbClasses;
    @FXML private TextArea txtInfoNotes;

    @FXML private ImageView avatar1;  @FXML private ImageView avatar2;  @FXML private ImageView avatar3;
    @FXML private ImageView avatar4;  @FXML private ImageView avatar5;  @FXML private ImageView avatar6;
    @FXML private ImageView avatar7;  @FXML private ImageView avatar8;  @FXML private ImageView avatar9;
    @FXML private ImageView avatar10; @FXML private ImageView avatar11; @FXML private ImageView avatar12;

    private final List<ImageView> avatarViews = new ArrayList<>();
    private Runnable onClose;
    private Consumer<InfoUpdate> onSave;

    private String selectedAvatarResource = null;
    private File selectedUploadFile = null;

    public static class InfoUpdate {
        public final String username;       // null means "keep old"
        public final String university;     // null means "keep old"
        public final String major;          // null means "keep old"
        public final String selectedClass;  // can be null
        public final String avatarResource; // can be null
        public final File uploadFile;       // can be null

        public InfoUpdate(String username, String university, String major,
                          String selectedClass, String avatarResource, File uploadFile) {
            this.username = username;
            this.university = university;
            this.major = major;
            this.selectedClass = selectedClass;
            this.avatarResource = avatarResource;
            this.uploadFile = uploadFile;
        }
    }

    @FXML
    public void initialize() {
        if (cmbClasses != null && cmbClasses.getItems().isEmpty()) {
            cmbClasses.getItems().addAll("CS102", "CS202", "MATH102");
        }

        // collect avatars safely
        addAvatar(avatar1,  "project/fxml/avatar1.png");
        addAvatar(avatar2,  "project/fxml/avatar2.png");
        addAvatar(avatar3,  "project/fxml/avatar3.png");
        addAvatar(avatar4,  "project/fxml/avatar4.png");
        addAvatar(avatar5,  "project/fxml/avatar5.png");
        addAvatar(avatar6,  "project/fxml/avatar6.png");
        addAvatar(avatar7,  "project/fxml/avatar7.png");
        addAvatar(avatar8,  "project/fxml/avatar8.png");
        addAvatar(avatar9,  "project/fxml/avatar9.png");
        addAvatar(avatar10, "project/fxml/avatar10.png");
        addAvatar(avatar11, "project/fxml/avatar11.png");
        addAvatar(avatar12, "project/fxml/avatar12.png");
    }

    private void addAvatar(ImageView iv, String resourcePath) {
        if (iv == null) return;
        avatarViews.add(iv);

        // ✅ click on avatar selects it
        iv.setOnMouseClicked(e -> selectAvatar(resourcePath));
    }

    // Called by mouse click or by FXML onAction if you use buttons
    private void selectAvatar(String resourcePath) {
        selectedAvatarResource = resourcePath;

        // simple selection highlight (optional)
        for (ImageView v : avatarViews) {
            if (v != null) v.setStyle("");
        }
        // try to highlight the clicked one (best effort)
        for (ImageView v : avatarViews) {
            if (v == null) continue;
            // can't reliably match resource, so just highlight all selected via border on the one user clicked visually
        }
    }

    // Optional: if your FXML uses onMouseClicked="#avatarClicked"
    @FXML
    private void avatarClicked(MouseEvent e) {
        Object src = e.getSource();
        if (src == avatar1) selectAvatar("project/fxml/avatar1.png");
        else if (src == avatar2) selectAvatar("project/fxml/avatar2.png");
        else if (src == avatar3) selectAvatar("project/fxml/avatar3.png");
        else if (src == avatar4) selectAvatar("project/fxml/avatar4.png");
        else if (src == avatar5) selectAvatar("project/fxml/avatar5.png");
        else if (src == avatar6) selectAvatar("project/fxml/avatar6.png");
        else if (src == avatar7) selectAvatar("project/fxml/avatar7.png");
        else if (src == avatar8) selectAvatar("project/fxml/avatar8.png");
        else if (src == avatar9) selectAvatar("project/fxml/avatar9.png");
        else if (src == avatar10) selectAvatar("project/fxml/avatar10.png");
        else if (src == avatar11) selectAvatar("project/fxml/avatar11.png");
        else if (src == avatar12) selectAvatar("project/fxml/avatar12.png");
    }

    public void setOnClose(Runnable onClose) { this.onClose = onClose; }
    public void setOnSave(Consumer<InfoUpdate> onSave) { this.onSave = onSave; }

    public void setInitialValues(String username, String university, String major, String classesText) {
        if (txtUsername != null) txtUsername.setText(username == null ? "" : username);
        if (txtUniversity != null) txtUniversity.setText(university == null ? "" : university);
        if (txtMajor != null) txtMajor.setText(major == null ? "" : major);
        // classesText is for display; your UI uses ComboBox, so we won't parse it here
    }

    @FXML
    private void upload() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a profile picture");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        Node anyNode = txtUsername != null ? txtUsername : (root != null ? root : null);
        if (anyNode == null || anyNode.getScene() == null) return;

        File file = chooser.showOpenDialog(anyNode.getScene().getWindow());
        if (file != null) selectedUploadFile = file;
    }

    @FXML
    private void save() {
        // ✅ empty fields => null => parent keeps old values
        String u = cleanOrNull(txtUsername);
        String uni = cleanOrNull(txtUniversity);
        String maj = cleanOrNull(txtMajor);

        String selectedClass = (cmbClasses != null) ? cmbClasses.getValue() : null;

        if (onSave != null) {
            onSave.accept(new InfoUpdate(u, uni, maj, selectedClass, selectedAvatarResource, selectedUploadFile));
        }

        close(); // ✅ always close after save
    }

    private String cleanOrNull(TextField tf) {
        if (tf == null) return null;
        String t = tf.getText();
        if (t == null) return null;
        t = t.trim();
        return t.isEmpty() ? null : t;
    }

    @FXML
    private void close() {
        // ✅ Always closes even if callbacks were not set
        if (onClose != null) onClose.run();
        else if (root != null) root.setVisible(false);
    }
}
