package futurepath.chat.server.controller;

import futurepath.chat.sockets.SocketThread;
import futurepath.chat.threads.ClientConnetion;
import java.awt.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection extends Thread {
    private int numberOfRooms;
    private int usersPerRoom;
    private ServerSocket serverSocket;
    private final static int PORT = 6666;
    private boolean connected = true;
    private Socket clientSocket = null;
    private final int LIM_MAX_USERS = 1000;
    private int contador = 0;   

    public Connection(int numberOfRooms, int usersPerRoom) throws IOException {
        this.numberOfRooms = numberOfRooms;
        this.usersPerRoom = usersPerRoom;
        serverSocket = new ServerSocket(PORT);
        serverSocket.setSoTimeout(1000000);
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
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

                        } catch (SocketException es) {
                            System.out.println("Server disconnected");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void close() throws IOException {
        serverSocket.close();
        setConnected(false);
    }

}
