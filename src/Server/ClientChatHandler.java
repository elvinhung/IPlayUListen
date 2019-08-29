package Server;

import java.net.*;

public class ClientChatHandler extends Thread {
  private Socket socket;

  public ClientChatHandler(Socket socket) {
    this.socket = socket;
  }
  @Override
  public void run() {

  }
}
