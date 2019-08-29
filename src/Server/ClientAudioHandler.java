package Server;

import java.net.Socket;

public class ClientAudioHandler extends Thread {
  private Socket socket;

  public ClientAudioHandler(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {

  }
}
