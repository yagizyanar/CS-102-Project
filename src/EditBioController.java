package project;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import java.util.function.Consumer;

public class EditBioController {

    @FXML private TextArea txtBio;

    private Runnable onClose;
    private Consumer<String> onSave;

    public void setInitialText(String bio) {
        if (txtBio != null) txtBio.setText(bio == null ? "" : bio);
    }

    public void setOnClose(Runnable onClose) { this.onClose = onClose; }
    public void setOnSave(Consumer<String> onSave) { this.onSave = onSave; }

    @FXML private void save() {
        if (onSave != null) onSave.accept(txtBio.getText());
        if (onClose != null) onClose.run();
    }

    @FXML private void close() {
        if (onClose != null) onClose.run();
    }
}