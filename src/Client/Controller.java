package Client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Controller {

  private Main client;
  private String userColor;

  public Controller() {}

  @FXML
  private void initialize() {
    userColor = ColorHelper.getRandomColorString();
    chatArea.setLineSpacing(5);
    playBtn.setDisable(true);
    nextBtn.setDisable(true);
  }

  @FXML private TextField textInput;
  @FXML private TextFlow chatArea;
  @FXML private ScrollPane chatScrollPane;
  @FXML private VBox userListPanel;
  @FXML private Button playBtn;
  @FXML private Button nextBtn;
  @FXML private VBox library;
  @FXML private ScrollPane libraryPane;

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

  public void enablePlayback() {
    playBtn.setDisable(false);
    nextBtn.setDisable(false);
  }

  private void playSong(String song) {
    JSONObject messageObj = new JSONObject();
    messageObj.put("type", "play_song");
    messageObj.put("song", song);
    client.getWriter().println(messageObj.toString());
    client.getWriter().flush();
  }

  public void createSongList(ArrayList<String> songList) {
    for (String song: songList) {
      if (!song.equals(".DS_Store")) {
        Button songBtn = createSongButton(song);
        songBtn.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
            playSong(song);
          }
        });
        library.getChildren().add(songBtn);
      }
    }
  }

  private Button createSongButton(String song) {
    System.out.println(song);
    Button btn = new Button();
    StringBuilder songName = new StringBuilder();
    for (int i = 0; i < song.length(); i++) {
      if (song.charAt(i) == '.') {
        break;
      }
      songName.append(song.charAt(i));
    }
    btn.setOnMouseEntered(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        btn.setTextFill(Color.CYAN);
      }
    });
    btn.setOnMouseExited(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        btn.setTextFill(Color.WHITE);
      }
    });
    Shape shape = new Rectangle(5,5, Color.YELLOW);
    btn.setGraphic(shape);
    btn.setGraphicTextGap(10);
    btn.setText(songName.toString());
    btn.setAlignment(Pos.CENTER_LEFT);
    btn.setTextFill(Color.WHITE);
    btn.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-font-size: 15px");
    btn.setMinHeight(32);
    btn.setMaxHeight(32);
    btn.prefWidthProperty().bind(libraryPane.widthProperty().divide(1.5));
    return btn;
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
