package Client;

import javafx.animation.StrokeTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class LoginController {

  private Main client;

  @FXML
  Line cursor;
  @FXML
  TextField username;

  public LoginController() {}

  @FXML
  private void initialize() {
    username.setFocusTraversable(false);
    StrokeTransition st = new StrokeTransition(Duration.millis(600), cursor, Color.WHITE, Color.BLACK);
    st.setCycleCount(Timeline.INDEFINITE);
    st.setAutoReverse(true);
    st.play();
  }

  @FXML
  private void connect() throws Exception {
    String user = username.getText();
    if (this.client != null && !user.trim().equals("")) {
      client.createMainStage(user);
    }
  }

  @FXML
  private void userEnter(KeyEvent keyEvent) throws Exception{
    if (keyEvent.getCode() == KeyCode.ENTER) {
      connect();
    }
  }

  public void setClient(Main client) {
    this.client = client;
  }



}
