package Server;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.net.Socket;

public class ClientAudioHandler extends Thread {
  private File soundFile = null;
  private Socket socket = null;
  private OutputStream out = null;

  public ClientAudioHandler(Socket socket) {
    this.socket = socket;
  }

  public void open() throws IOException {
    out = socket.getOutputStream();
  }

  public void close() throws IOException {
    if (socket != null) socket.close();
    if (out != null) out.close();
  }

  public void setSoundFile(File soundFile) {
    this.soundFile = soundFile;
  }

  @Override
  public void run() {
    while (out != null) {
      try {
        if (soundFile != null) {
          FileInputStream bis = new FileInputStream(soundFile);
          byte buffer[] = new byte[2048];
          int count;
          while ((count = bis.read(buffer)) != -1) {
            out.write(buffer, 0, count);
          }
          System.out.println("done writing audio to client");
          soundFile = null;
        }
        Thread.sleep(100);
      } catch (Exception e) { }
    }
  }
}
