package Server;

import javax.sound.sampled.AudioSystem;
import java.io.File;

public class ServerMain {

  private Server audioServer;
  private Server chatServer;

  public ServerMain() {
    chatServer = new Server(8080, true, this);
    audioServer = new Server(8081, false, this);
  }

  public void playAll(String fileName) {
    System.out.println("play All with: " + fileName);
    File audioFile = new File("src/Audio/" + fileName);
    try {
      System.out.println(AudioSystem.getAudioFileFormat(audioFile));

    } catch (Exception e) {
      e.printStackTrace();
    }
    audioServer.playAll(audioFile);
  }

  public static void main(String args[]) {
    new ServerMain();
  }

}
