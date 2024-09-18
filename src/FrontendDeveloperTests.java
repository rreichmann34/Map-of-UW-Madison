import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import javafx.application.Platform;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

public class FrontendDeveloperTests extends ApplicationTest {

    private FrontendInterface frontend;
    private Pane testPane;

    @Override
    public void start(Stage stage) {
        testPane = new Pane();
        stage.setScene(new javafx.scene.Scene(testPane, 500, 500));
        stage.show();
    }

    @BeforeEach
    public void setup() {
        // This should be replaced with the actual frontend class
        frontend = new Frontend();  
    }

    @Test
    public void testCreateAllControls() throws InterruptedException {
        Platform.runLater(() -> {
            frontend.createAllControls(testPane);
        });
        Thread.sleep(1000); // wait for the updates to complete
        assertFalse(testPane.getChildren().isEmpty(), "Pane should not be empty after adding controls.");
    }

    @Test
    public void testCreateShortestPathControls() throws InterruptedException {
        Platform.runLater(() -> {
            frontend.createShortestPathControls(testPane);
        });
        Thread.sleep(1000); // wait for the updates to complete
        assertEquals(1, testPane.getChildren().stream().filter(node -> node instanceof Button).count(),
            "There should be at least one button for shortest path controls.");
    }

    @Test
    public void testCreatePathListDisplay() throws InterruptedException {
        Platform.runLater(() -> {
            frontend.createPathListDisplay(testPane);
        });
        Thread.sleep(1000); // wait for the updates to complete
        assertEquals(1, testPane.getChildren().stream().filter(node -> node instanceof Label).count(),
            "There should be at least one label for displaying paths.");
    }

    @Test
    public void testCreateAdditionalFeatureControls() throws InterruptedException {
        Platform.runLater(() -> {
            frontend.createAdditionalFeatureControls(testPane);
        });
        Thread.sleep(1000); // wait for the updates to complete
        assertTrue(testPane.getChildren().size() > 0, "Should have additional controls added to the pane.");
    }
}
