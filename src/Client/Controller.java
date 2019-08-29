package Client;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Controller {

  public Controller() {}

  @FXML
  private void initialize() {}

  @FXML
  private TextField textInput;
  @FXML
  private TextFlow chatArea;
  @FXML
  private ScrollPane chatScrollPane;

  @FXML
  private void sendText() {
    Text username = new Text(textInput.getText());
    username.setFill(Color.BLUEVIOLET);
    Text text = new Text(": " + textInput.getText());
    if (chatArea.getChildren().size() != 0) {
      chatArea.getChildren().add(new Text(System.lineSeparator()));
    }
    chatArea.getChildren().addAll(username, text);
    chatScrollPane.layout();
    chatScrollPane.setVvalue(1.0);
    textInput.clear();
  }
}
