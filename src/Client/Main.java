package Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Main extends Application {
  private BufferedReader reader;
  private PrintWriter writer;
  private Controller controller;

  public void run() {
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception{
    Stage loginStage = new Stage();
    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
    Parent loginRoot = loginLoader.load();
    LoginController loginController = loginLoader.getController();
    loginController.setClient(this);
    Scene loginScene = new Scene(loginRoot);
    loginScene.getStylesheets().add(getClass().getResource("/Styles/LoginStyles.css").toExternalForm());
    loginStage.setScene(loginScene);
    loginStage.show();
  }

  public void createMainStage(String username) throws Exception {
    Stage primaryStage = new Stage();
    if (!setUpNetworking("localhost", 8080)) return;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
    Parent root = loader.load();
    controller = loader.getController();
    controller.setClient(this);
    Scene scene = new Scene(root, 950, 700);
    scene.getStylesheets().add(getClass().getResource("/Styles/IPlayUListenStyles.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }


  public static void main(String[] args) {
    try {
      new Main().run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean setUpNetworking(String IP, int port) {
    try {
      Socket socket = new Socket(IP, port);
      InputStreamReader input = new InputStreamReader(socket.getInputStream());
      OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());
      reader = new BufferedReader(input);
      writer = new PrintWriter(output);
      System.out.println("Networking established with port: " + port);
      Thread readerThread = new Thread(new IncomingReader());
      readerThread.start();
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public void handleMessage(String message) {
    controller.receiveText(message);
  }

  public PrintWriter getWriter() {
    return writer;
  }

  class IncomingReader implements Runnable {
    @Override
    public void run() {
      String message;
      try {
        while ((message = reader.readLine()) != null) {
          String msg = new String(message);
          Platform.runLater(() -> handleMessage(msg));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
