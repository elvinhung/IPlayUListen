package Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.sound.sampled.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Main extends Application {

  private JSONParser parser = new JSONParser();
  private Socket audioSocket;
  private Socket chatSocket;
  private BufferedReader reader;
  private PrintWriter writer;
  private Controller controller;
  private String username;
  private Stage loginStage;
  private IncomingReader incomingReader;
  private IncomingAudio incomingAudio;
  private InputStream audioIn;

  public void run() {
    launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception{
    loginStage = new Stage();
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
    if (!setUpChatNetworking("localhost", 8080) || !setUpAudioNetworking("localhost", 8081)) return;
    loginStage.close();

    FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
    Parent root = loader.load();

    this.username = username;
    this.controller = loader.getController();
    this.controller.setClient(this);

    sendUserJoined(username);

    Scene scene = new Scene(root, 950, 700);
    scene.getStylesheets().add(getClass().getResource("/Styles/IPlayUListenStyles.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent windowEvent) {
        try {
          System.out.println("goodbye");
          primaryStage.close();
        } catch (Exception e) {

        }
      }
    });
  }


  public static void main(String[] args) {
    try {
      new Main().run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean setUpChatNetworking(String IP, int port) {
    try {
      chatSocket = new Socket(IP, port);
      InputStreamReader input = new InputStreamReader(chatSocket.getInputStream());
      OutputStreamWriter output = new OutputStreamWriter(chatSocket.getOutputStream());
      reader = new BufferedReader(input);
      writer = new PrintWriter(output);
      System.out.println("chat networking established with port: " + port);
      incomingReader = new IncomingReader();
      Thread readerThread = new Thread(incomingReader);
      readerThread.start();
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private boolean setUpAudioNetworking(String IP, int port) {
    try {
      audioSocket = new Socket(IP, port);
      audioIn = new BufferedInputStream(audioSocket.getInputStream());
      System.out.println("Audio networking established with port: " + port);
      incomingAudio = new IncomingAudio();
      Thread readerThread = new Thread(incomingAudio);
      readerThread.start();
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private void sendUserJoined(String user) {
    JSONObject userJoinedObj = new JSONObject();
    userJoinedObj.put("type", "user_joined");
    userJoinedObj.put("user", user);
    writer.println(userJoinedObj);
    writer.flush();
  }

  public void handleMessage(String JSONString) {
    try {
      JSONObject messageObj = (JSONObject) parser.parse(JSONString);
      String type = (String) messageObj.get("type");
      switch (type) {
        case "message": {
          String user = (String) messageObj.get("user");
          String message = (String) messageObj.get("message");
          String color = (String) messageObj.get("color");
          controller.receiveText(user, message, color);
          break;
        }
        case "user_joined": {
          String user = (String) messageObj.get("user");
          if (messageObj.get("isHost") != null) {
            controller.enablePlayback();
            controller.addUser(user, true);
          } else {
            controller.addUser(user, false);
          }
          break;
        }
        case "user_list": {
          String host = (String) messageObj.get("host");
          JSONArray users = (JSONArray) messageObj.get("users");
          users.forEach((user) -> {
            String username= (String) user;
            if (!this.username.equals(username)) {
              if (host.equals(username)) {
                controller.addUser(username, true);
              } else {
                controller.addUser(username, false);
              }
            }
          });
          break;
        }
        case "new_host": {
          String user = (String) messageObj.get("user");
          controller.setHost(user);
          break;
        }
        case "pause": {
          incomingAudio.stopClip();
          break;
        }
        case "song_list": {
          ArrayList<String> songList = new ArrayList<>();
          JSONArray songs = (JSONArray) messageObj.get("songs");
          songs.forEach(song -> {
            songList.add((String) song);
          });
          controller.createSongList(songList);
          break;
        }
        default: {
          System.out.println("Invalid type of message");
        }
      }
    } catch (Exception e) {
      System.out.println("Unable to parse JSON");
    }
  }

  public PrintWriter getWriter() {
    return writer;
  }

  public String getUsername() {
    return username;
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

  class IncomingAudio implements Runnable {

    private boolean isOpen = true;
    private Clip clip = null;

    public synchronized void close() {
      isOpen = false;
    }

    public void stopClip() {
      System.out.println("attempting to stop clip");
      if (clip != null && clip.isActive()) {
        clip.close();
        System.out.println("clip closed");
      }
    }

    @Override
    public void run() {
//      File file = new File("src/Audio/test_file.wav");
//      System.out.println(file.getAbsolutePath());
      try {
        //InputStream a = new BufferedInputStream(new FileInputStream(file));
//        AudioFormat format = new AudioFormat((float) 41000.0, 16, 2, true, false);
//        AudioInputStream ais = new AudioInputStream(audioIn, format, 25840);
        AudioInputStream ais = AudioSystem.getAudioInputStream(audioIn);
        clip = AudioSystem.getClip();
        System.out.println("clip created");
        clip.open(ais);
        clip.start();
        System.out.println("clip playing");
        Thread.sleep(100);
        clip.drain();
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println("done");
    }
//      while (isOpen) {
//        try {
//          AudioInputStream ais = AudioSystem.getAudioInputStream(audioIn);
//          AudioFormat format = ais.getFormat();
//          DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
//          SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
//          audioLine.open(format);
//          audioLine.start();
//
//          System.out.println("Playback started.");
//
//          byte[] bytesBuffer = new byte[2048];
//          int bytesRead = -1;
//
//          while ((bytesRead = ais.read(bytesBuffer)) != -1) {
//            audioLine.write(bytesBuffer, 0, bytesRead);
//          }
//
//          audioLine.drain();
//          audioLine.close();
//          ais.close();
//
//          System.out.println("Playback completed.");
//
////          Clip clip = AudioSystem.getClip();
////          clip.open(ais);
////          clip.start();
////          Thread.sleep(100);
////          clip.drain();
//        } catch (Exception e) {
//          System.out.println("Exception playing audio");
//          e.printStackTrace();
//        }
//      }
//    }
  }

}
