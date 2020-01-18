package futurepath.chat.server.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection extends Thread {

    private int numberOfRooms;
    private int usersPerRoom;
    private ServerSocket serverSocket;
    private final static int PORT = 6666;
    private boolean connected = true;
    private Socket server = null;

    public Connection(int numberOfRooms, int usersPerRoom) throws IOException {
        this.numberOfRooms = numberOfRooms;
        this.usersPerRoom = usersPerRoom;
        serverSocket = new ServerSocket(PORT);
        serverSocket.setSoTimeout(10000);

    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void run() {
        try {
            server = serverSocket.accept();    
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
 
    public static void main(String [] args) {
      
   }

    public void close() throws IOException {
        server.close();
    }

}
