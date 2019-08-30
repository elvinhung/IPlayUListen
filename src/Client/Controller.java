package Client;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Controller {

  private Main client;

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
    client.getWriter().println(textInput.getText());
    client.getWriter().flush();
    textInput.clear();
  }
  @FXML
  private void sendTextEnter(KeyEvent keyEvent) {
    if (keyEvent.getCode() == KeyCode.ENTER) {
      sendText();
    }
  }

  public void receiveText(String message) {
    Text username = new Text(message);
    username.setFill(Color.BLUEVIOLET);
    Text text = new Text(": " + message);
    text.setFill(Color.WHITE);
    if (chatArea.getChildren().size() != 0) {
      chatArea.getChildren().add(new Text(System.lineSeparator()));
    }
    chatArea.getChildren().addAll(username, text);
    chatScrollPane.layout();
    chatScrollPane.setVvalue(1.0);
  }

  public void setClient(Main client) {
    this.client = client;
  }
}
