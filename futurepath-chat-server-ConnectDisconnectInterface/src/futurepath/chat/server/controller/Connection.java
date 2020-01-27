package futurepath.chat.server.controller;

import futurepath.chat.threads.ClientConnetion;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection extends Thread {
    private final static int PORT = 6666;
    private final int LIM_MAX_USERS = 1000;
    
    //attributes
    private int numberOfRooms;
    private int usersPerRoom;
    private ServerSocket serverSocket;
    private boolean connected = true;
    private Socket clientSocket = null;
    private int contador = 0;   
   
    //constructor with parameters
    public Connection(int numberOfRooms, int usersPerRoom) throws IOException {
        this.numberOfRooms = numberOfRooms;
        this.usersPerRoom = usersPerRoom;
        this.serverSocket = new ServerSocket(PORT);
        this.serverSocket.setSoTimeout(1000000);
    }
    //setter
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public void run() {
        synchronized (this) {
            while (connected || contador < LIM_MAX_USERS) {
                try {
                    if (serverSocket != null) {
                        System.out.println("Waiting for client on port "
                                + serverSocket.getLocalPort() + "...");
                        try {
                            clientSocket = serverSocket.accept();
                            contador++; 
                            Thread newUser = new ClientConnetion(clientSocket);
                            newUser.start();     
                        } catch (SocketException se) {
                            System.out.println("Server disconnected");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    //other methods
    public void close() throws IOException {
        serverSocket.close();
        setConnected(false);
    }

}
