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

  public boolean createMainStage(String username, String serverName) throws Exception {
    Stage primaryStage = new Stage();
    if (!setUpChatNetworking(serverName, 8080) || !setUpAudioNetworking(serverName, 8081)) return false;
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
          incomingAudio.stopClip();
          incomingAudio.close();
          primaryStage.close();
          System.exit(0);
        } catch (Exception e) { }
      }
    });
    return true;
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
      System.out.println(type);
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
            controller.setHost();
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
          break;
        }
        case "play": {
          controller.setPlay(true);
          incomingAudio.playClip();
          break;
        }
        case "pause": {
          controller.setPlay(false);
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
        case "play_song": {
          String songName = (String) messageObj.get("song");
          System.out.println(songName);
          controller.setPlay(true);
          controller.setSong(songName);
          break;
        }
        default: {
          System.out.println("Invalid type of message");
        }
      }
    } catch (Exception e) {
      System.out.println("Unable to parse JSON");
      e.printStackTrace();
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
    private long position = 0;

    public void close() {
      isOpen = false;
    }

    public void stopClip() {
      System.out.println("attempting to stop clip");
      if (clip != null && clip.isActive()) {
        position = clip.getMicrosecondPosition();
        clip.stop();
        System.out.println(position);
        System.out.println("clip stopped");
      }
    }

    public void playClip() {
      System.out.println("attempting to play clip");
      if (clip != null) {
        clip.setMicrosecondPosition(position);
        clip.start();
        System.out.println("clip started");
      }
    }

    @Override
    public void run() {
//      File file = new File("src/Audio/test_file.wav");
//      System.out.println(file.getAbsolutePath());

      while (isOpen) {
        try {
          AudioInputStream ais = AudioSystem.getAudioInputStream(audioIn);
          clip = AudioSystem.getClip();
          System.out.println("clip created");
          clip.open(ais);
          clip.start();
          System.out.println("clip playing");
//        Thread.sleep(100);
//        clip.drain();
        } catch (Exception e) {
          e.printStackTrace();
        }
        System.out.println("done");
      }
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
