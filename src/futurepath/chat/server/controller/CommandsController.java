package futurepath.chat.server.controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CommandsController { 
    private int numrooms = 99;
    private static HashMap<Socket, String> usersConnected = new HashMap<>();
    private static HashMap<String, ArrayList<String>> usersInTheRoom = new HashMap<>(); 
    private static ArrayList<Thread> threads = new ArrayList<>();
    
    public void interpret(String[] args, Socket socket) {
        String username = usersConnected.get(socket);
        System.out.println(args[0]);
        switch (args[0]) {                
            case "CONNECT":
                if(userExists(username)) {
                    respond("Nombre de usuario ya existe.", socket);
                } else {
                    usersConnected.put(socket, username);
                    updateRoomsClient(socket);
                }
                break;
            case "JOIN":
                String room = args[1];
                if(usersInTheRoom.containsKey(room)) {
                    if(usersInTheRoom.containsValue(username)) {
                        respond("Ya estas dentro de este room", socket);
                    } else {
                        usersInTheRoom.get(room).add(username);
                    }
                }else {
                    if(usersInTheRoom.size() < numrooms) {
                        ArrayList<String> users = new ArrayList<>();
                        users.add(username);
                        usersInTheRoom.put(room, users);
                        System.out.println("CREADO");
                        usersInTheRoom.toString();
                    } else {
                        respond("No existe este room y no se puede crear porque se ha alcanzado el limite de rooms.",socket);
                    }
                }
                break;
            case "LEAVE":
                if(usersInTheRoom.containsKey(args[1])) {
                    usersInTheRoom.get(args[1]).remove(username);
                    if(usersInTheRoom.get(args[1]).isEmpty()) {
                        usersInTheRoom.remove(args[1]);
                    }
                }
                break;
            case "SEND":
                String[] messageArray = new String[args.length-2];
                String messageZ = (new StringBuilder().append(username).append(args[1])).toString();
                String messageX = String.join(",", messageArray);
                String message = messageZ + messageX;
                if(usersInTheRoom.containsKey(args[1])) {
                    ArrayList<Socket> socketsToSend = getSocketsOfClients(usersInTheRoom.get(args[1]));
                    for(int i = 0; i < socketsToSend.size(); i++) {
                        respond(message,socketsToSend.get(i));
                    }
                } else {
                    respond("Estas enviando mensaje a un room que no existe.", socket);
                }
                break;
            case "DISCONNECT":
                if(userExists(username)) {
                    try {
                        usersConnected.remove(socket);
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    respond("No estas conectado.", socket);
                }
                break;
        }
    }
    private ArrayList<Socket> getSocketsOfClients(ArrayList<String> users) {
        ArrayList<Socket> sockets = new ArrayList<>();
        usersConnected.forEach((socket, user) -> {
            if(users.contains(user))
                sockets.add(socket);
        });
        return sockets;
    }
    private void updateRoomsClient(Socket socket) {
        try {
            String message = "ROOM";
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            for(int i = 0; i < usersInTheRoom.size(); i++) {
                message = (new StringBuilder().append(" ").append(usersInTheRoom.get(i))).toString();
            }
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateClientsInRoom(String room, Socket socket) {
        String message = "USERROOM";
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            ArrayList<String> users = usersInTheRoom.get(room);
            for(int i = 0; i < users.size(); i++) {
                message = (new StringBuilder().append(" ").append(users.get(i))).toString();
            }
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void updateAllClients(Socket socket) {
        String message = "USER";
        ArrayList<String> users = (ArrayList<String>) usersConnected.values();
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            for(int i = 0; i < usersConnected.size(); i++) {
                message = (new StringBuilder().append(" ").append(users.get(i)).toString());
            }
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void respond(String message, Socket socket){
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startThread(String username, Socket socket) {
        
    }

    private boolean userExists(String name) {
        return usersConnected.containsValue(name);
    }
}