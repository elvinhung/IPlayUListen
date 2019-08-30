package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Main extends Application {
  private BufferedReader reader;
  private PrintWriter writer;

  public void run() {
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception{
    if (!setUpNetworking("localhost", 8080)) return;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
    Parent root = loader.load();
    Controller controller = loader.getController();
    controller.setClient(this);
    primaryStage.setScene(new Scene(root, 950, 700));
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

  public PrintWriter getWriter() {
    return writer;
  }

  class IncomingReader implements Runnable {
    @Override
    public void run() {
      String message;
      try {
        while ((message = reader.readLine()) != null) {
          System.out.println("Client received: " + message);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
