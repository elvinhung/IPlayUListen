package Server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;


public class Server implements Runnable {
  private ArrayList<ClientAudioHandler> audioClients = new ArrayList<ClientAudioHandler>();
  private ArrayList<ClientChatHandler> chatClients = new ArrayList<ClientChatHandler>();
  private ServerSocket server = null;
  private Thread thread = null;
  private int clientCount = 0;
  private boolean isRunning = true;
  private boolean isChatServer;

  public Server(int port, boolean isChatServer) {
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

  private void addThread(Socket socket) {
    System.out.println("Client accepted: " + socket);
    if (isChatServer) {
      ClientChatHandler client = new ClientChatHandler(socket);
      chatClients.add(client);
      try {
        client.start();
        clientCount++;
      } catch (Exception e) {
        System.out.println("Error opening thread: " + e);
      }
    } else {
      ClientAudioHandler client = new ClientAudioHandler(socket);
      audioClients.add(client);
      try {
        client.start();
        clientCount++;
      } catch (Exception e) {
        System.out.println("Error opening thread: " + e);
      }
    }
  }

  public static void main(String args[]) {
    Server audioServer = new Server(8080, true);
    Server chatServer = new Server(8081, false);
  }
}


