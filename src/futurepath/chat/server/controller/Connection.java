package futurepath.chat.server.controller;

import futurepath.chat.sockets.SocketThread;
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
        synchronized (this) {
            while (connected) {
                try {
                    if (serverSocket != null) {
                        System.out.println("Waiting for client on port "
                                + serverSocket.getLocalPort() + "...");
                        try {
                            clientSocket = serverSocket.accept();
                           
                            
                            /*System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
                            DataInputStream in = new DataInputStream(clientSocket.getInputStream());

                            System.out.println(in.readUTF());
                            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                            out.writeUTF("Thank you for connecting to " + clientSocket.getLocalSocketAddress()
                                    + "\nGoodbye!");*/
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
