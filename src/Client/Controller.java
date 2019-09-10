package Client;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

  @FXML
  private void play() {
    JSONObject messageObj = new JSONObject();
    messageObj.put("type", "play");
    messageObj.put("file", "test_file");
    client.getWriter().println(messageObj.toString());
    client.getWriter().flush();
    System.out.println("sent play message");
  }

  @FXML
  private void pause() {
    JSONObject messageObj = new JSONObject();
    messageObj.put("type", "pause");
    client.getWriter().println(messageObj.toString());
    client.getWriter().flush();
    System.out.println("sent pause message");
  }

  public void addUser(String username, boolean isHost) {
    Circle circle;
    if (isHost) {
      circle = new Circle(4.5, Color.BLUEVIOLET);
    } else {
      circle = new Circle(4.5, Color.LIMEGREEN);
    }
    ToggleButton user = new ToggleButton();
    user.setGraphic(circle);
    user.setGraphicTextGap(5);
    user.setAlignment(Pos.CENTER_LEFT);
    user.setText(username);
    user.setStyle(" -fx-text-fill: white; -fx-background-color: black; -fx-border-color: white; -fx-font-size: 15px");
    user.setMinHeight(32);
    user.setMaxHeight(32);
    user.setMinWidth(180);
    user.setMaxWidth(180);
    userListPanel.getChildren().add(user);
  }

  public void setHost(String user) {
    Circle circle = new Circle(4.5, Color.BLUEVIOLET);
    for (Node e: userListPanel.getChildren()) {
      ToggleButton btn = (ToggleButton) e;
      if (btn.getText().equals(user)) {
        btn.setGraphic(circle);
      }
    }
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
