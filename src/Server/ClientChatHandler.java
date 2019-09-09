package Server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.*;

public class ClientChatHandler extends Thread {
  private JSONParser parser = new JSONParser();
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
        JSONObject messageObj = (JSONObject) parser.parse(message);
        String type = (String) messageObj.get("type");
        if (type.equals("user_joined")) {
          String user = (String) messageObj.get("user");
          server.addUser(user);
        } else if (type.equals("play")) {
          String fileName = (String) messageObj.get("file");
          server.play(fileName);
        }
        server.sendToAll(message);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public PrintWriter getWriter() {
    return this.writer;
  }
}
