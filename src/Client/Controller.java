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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

import java.io.File;
import java.util.ArrayList;

public class Controller {

  private Main client;
  private String userColor;
  private boolean isHost;
  private File playImg = new File("src/play.png");
  private File pauseImg = new File("src/pause.png");
  private boolean isPlaying = false;

  public Controller() {}

  @FXML
  private void initialize() {
    userColor = ColorHelper.getRandomColorString();
    chatArea.setLineSpacing(5);
    playBtn.setDisable(true);
    nextBtn.setDisable(true);
    prevBtn.setDisable(true);
    isHost = false;
  }

  @FXML private TextField textInput;
  @FXML private TextFlow chatArea;
  @FXML private ScrollPane chatScrollPane;
  @FXML private VBox userListPanel;
  @FXML private Button playBtn;
  @FXML private Button nextBtn;
  @FXML private Button prevBtn;
  @FXML private VBox library;
  @FXML private ScrollPane libraryPane;
  @FXML private ToggleButton songText;
  @FXML private ImageView playBtnImg;

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
    if (isPlaying) {
      JSONObject messageObj = new JSONObject();
      messageObj.put("type", "pause");
      client.getWriter().println(messageObj.toString());
      client.getWriter().flush();
      System.out.println("sent pause message");
    } else {
      JSONObject messageObj = new JSONObject();
      messageObj.put("type", "play");
      client.getWriter().println(messageObj.toString());
      client.getWriter().flush();
      System.out.println("sent play message");
    }
  }


  public void enablePlayback() {
    playBtn.setDisable(false);
    nextBtn.setDisable(false);
    prevBtn.setDisable(false);
  }

  private void playSong(String song) {
    JSONObject messageObj = new JSONObject();
    messageObj.put("type", "play_song");
    messageObj.put("song", song);
    client.getWriter().println(messageObj.toString());
    client.getWriter().flush();
  }

  public void setSong(String song) {
    songText.setText(cleanSongName(song));
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
        songBtn.setDisable(true);
        library.getChildren().add(songBtn);
      }
    }
  }

  private String cleanSongName(String song) {
    StringBuilder songName = new StringBuilder();
    for (int i = 0; i < song.length(); i++) {
      if (song.charAt(i) == '.') {
        break;
      }
      songName.append(song.charAt(i));
    }
    return songName.toString();
  }

  private Button createSongButton(String song) {
    Button btn = new Button();
    String songName = cleanSongName(song);
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
    btn.setText(songName);
    btn.setAlignment(Pos.CENTER_LEFT);
    btn.setTextFill(Color.WHITE);
    btn.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-font-size: 15px");
    btn.setMinHeight(32);
    btn.setMaxHeight(32);
    btn.prefWidthProperty().bind(libraryPane.widthProperty().divide(1.5));
    return btn;
  }

  public void setPlay(boolean isPlaying) {
    this.isPlaying = isPlaying;
    File imgFile = isPlaying ? pauseImg : playImg;
    Image img = new Image(imgFile.toURI().toString());
    playBtnImg.setImage(img);
    playBtn.setAlignment(Pos.CENTER);
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

  public void setHost() {
    isHost = true;
    for (Node node: library.getChildren()) {
      Button btn = (Button) node;
      btn.setDisable(false);
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
