package Server;

import org.json.simple.JSONObject;

import java.net.*;
import java.io.*;
import java.util.ArrayList;


public class Server implements Runnable {
  private ServerMain serverMain;
  private ArrayList<ClientAudioHandler> audioClients = new ArrayList<ClientAudioHandler>();
  private ArrayList<ClientChatHandler> chatClients = new ArrayList<ClientChatHandler>();
  private ServerSocket server = null;
  private Thread thread = null;
  private boolean isRunning = true;
  private boolean isChatServer;
  private ArrayList<String> users = new ArrayList<>();
  private String host = null;
  private ClientChatHandler hostClient;

  public Server(int port, boolean isChatServer, ServerMain serverMain) {
    this.serverMain = serverMain;
    this.isChatServer = isChatServer;
    try {
      System.out.println("Binding to port " + port + ", please wait  ...");
      server = new ServerSocket(port);
      System.out.println("Server started: " + server);
      start();
    } catch(IOException e) {
      System.out.println("Can not bind to port " + port + ": " + e.getMessage());
    }
  }

  public void run() {
    while (thread != null && isRunning) {
      try {
        System.out.println("Waiting for a client ...");
        addThread(server.accept());
      } catch(IOException e) {
        System.out.println("Server accept error: " + e);
        stop();
      }
    }
  }

  public void start() {
    if (thread == null) {
      thread = new Thread(this);
      thread.start();
    }
  }

  public void stop() {
    if (thread != null) {
      isRunning = false;
      thread = null;
    }
  }

//  private int findClient(int ID) {
//    for (int i = 0; i < clientCount; i++) {
//      if (clients[i].getID() == ID) {
//        return i;
//      }
//    }
//    return -1;
//  }

//  public synchronized void handle(int ID, String input) {
//    if (input.equals(".bye")) {
//      clients[findClient(ID)].send(".bye");
//      remove(ID);
//    } else {
//      for (int i = 0; i < clientCount; i++) {
//        clients[i].send(ID + ": " + input);
//      }
//    }
//  }
//
//  public synchronized void remove(int ID) {
//    int pos = findClient(ID);
//    if (pos >= 0) {
//      ServerThread toTerminate = clients[pos];
//      System.out.println("Removing client thread " + ID + " at " + pos);
//      if (pos < clientCount-1) {
//        for (int i = pos + 1; i < clientCount; i++) {
//          clients[i - 1] = clients[i];
//        }
//        clientCount--;
//      }
//      try {
//        toTerminate.close();
//      } catch(IOException e) {
//        System.out.println("Error closing thread: " + e);
//      }
//      toTerminate.stop(); }
//  }

  public int getUsersLength() {
    return this.users.size();
  }

  public void sendToAll(String message) {
    chatClients.forEach((client) -> {
      client.getWriter().println(message);
      client.getWriter().flush();
    });
  }

  private void addThread(Socket socket) {
    System.out.println("Client accepted: " + socket);
    if (isChatServer) {
      ClientChatHandler client = new ClientChatHandler(this, socket);
      chatClients.add(client);
      try {
        client.open();
        client.start();
        System.out.println("Chat client started");
      } catch (Exception e) {
        System.out.println("Error opening thread: " + e);
      }
    } else {
        ClientAudioHandler client = new ClientAudioHandler(socket);
        audioClients.add(client);
        System.out.println("Audio client created");
        try {
          client.open();
        } catch (Exception e) {
          System.out.println("Error opening thread: " + e);
        }
    }
  }

  public void play(String fileName) {
    this.serverMain.playAll(fileName);
  }

  public void playAll(File audioFile) {
    audioClients.forEach(client -> {
      client.setSoundFile(audioFile);
      client.start();
    });
  }

  public synchronized void addUser(String user) {
    users.add(user);
  }

  public void sendUsernames(ClientChatHandler client) {
    if (users.size() > 0) {
      System.out.println("sending usernames to clients");
      JSONObject usersObj = new JSONObject();
      usersObj.put("type", "user_list");
      usersObj.put("users", this.users);
      usersObj.put("host", this.host);
      client.getWriter().println(usersObj.toString());
      client.getWriter().flush();
    }
  }

  private void sendNewHost(ClientChatHandler client) {
    JSONObject usersObj = new JSONObject();
    usersObj.put("type", "new_host");
    usersObj.put("user", this.host);
    client.getWriter().println(usersObj.toString());
    client.getWriter().flush();
  }

  public void setHost(String user, ClientChatHandler hostClient) {
    this.host = user;
    this.hostClient = hostClient;
  }
}


