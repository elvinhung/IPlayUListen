package Server;

import java.io.*;
import java.net.*;

public class ClientChatHandler extends Thread {
  private Socket socket;
  private Server server;
  private BufferedReader reader;
  private PrintWriter writer;

  public ClientChatHandler(Server server, Socket socket) {
    this.socket = socket;
    this.server = server;
  }

  public void open() throws IOException {
    OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
    InputStreamReader in = new InputStreamReader(socket.getInputStream());
    reader = new BufferedReader(in);
    writer = new PrintWriter(out);
  }

  public void close() throws IOException {
    if (socket != null) socket.close();
    if (reader != null) reader.close();
    if (writer != null) writer.close();
  }

  @Override
  public void run() {
    String message;
    try {
      while ((message = reader.readLine()) != null) {
        server.sendToAll(message);
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e);
    }
  }

  public PrintWriter getWriter() {
    return this.writer;
  }
}
