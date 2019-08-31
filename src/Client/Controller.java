package Client;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.json.simple.JSONObject;

public class Controller {

  private Main client;
  private String userColor;

  public Controller() {}

  @FXML
  private void initialize() {
    userColor = ColorHelper.getRandomColorString();
    chatArea.setLineSpacing(5);
  }

  @FXML
  private TextField textInput;
  @FXML
  private TextFlow chatArea;
  @FXML
  private ScrollPane chatScrollPane;
  @FXML
  private VBox userListPanel;

  @FXML
  private void sendText() {
    String text = textInput.getText();
    if (!text.equals("")) {
      JSONObject messageObj =  new JSONObject();
      messageObj.put("type", "message");
      messageObj.put("user", client.getUsername());
      messageObj.put("message", text);
      messageObj.put("color", userColor);
      client.getWriter().println(messageObj.toString());
      client.getWriter().flush();
      textInput.clear();
    }
  }
  @FXML
  private void sendTextEnter(KeyEvent keyEvent) {
    if (keyEvent.getCode() == KeyCode.ENTER) {
      sendText();
    }
  }

  public void addUser(String username) {
    Circle circle = new Circle(5, Color.LIMEGREEN);
    ToggleButton user = new ToggleButton();
    user.setGraphic(circle);
    user.setGraphicTextGap(5);
    user.setAlignment(Pos.CENTER_LEFT);
    user.setText(username);
    user.setStyle(" -fx-text-fill: white; -fx-background-color: black; -fx-border-color: white; -fx-font-size: 15px");
    user.setMinHeight(30);
    user.setMaxHeight(30);
    user.setMinWidth(170);
    user.setMaxWidth(170);
    userListPanel.getChildren().add(user);
  }

  public void receiveText(String username, String message, String color) {
    Text user = new Text(username);
    user.setFill(ColorHelper.getColor(color));
    Text text = new Text(": " + message);
    text.setFill(Color.WHITE);
    if (chatArea.getChildren().size() != 0) {
      chatArea.getChildren().add(new Text(System.lineSeparator()));
    }
    chatArea.getChildren().addAll(user, text);
    chatScrollPane.layout();
    chatScrollPane.setVvalue(1.0);
  }

  public void setClient(Main client) {
    this.client = client;
  }
}
