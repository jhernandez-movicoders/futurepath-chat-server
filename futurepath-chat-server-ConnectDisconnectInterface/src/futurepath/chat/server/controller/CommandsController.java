package futurepath.chat.server.controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandsController { 
    
    //attributes
    private int numrooms = 99;
    //stores the connected users
    private static HashMap<Socket, String> usersConnected = new HashMap<>();
    //stores the room and its users
    private static HashMap<String, ArrayList<String>> roomAndUsers = new HashMap<>();
    
    private static ArrayList<Thread> threads = new ArrayList<>();
    
    //methods
    
    /**
     * @param inputText: commands that the client sends
     * @param socket: socket of the client
     */
    public void interpret(String[] inputText, Socket socket) {
        ArrayList<String> as = new ArrayList<>();
        as.add("a");
        roomAndUsers.put("aaa", as);
        String username = usersConnected.get(socket);
        System.out.println(inputText[0].toString() + inputText[1].toString());
        //check(inputText);
        switch (inputText[0]) {                
            case "CONNECT":
                if(userExists(username)) {
                    respond("ERROR Nombre de usuario ya existe.", socket);
                } else {
                    //adds the user to the list and updates the room
                    usersConnected.put(socket, username);
                    updateRoomsClient(socket);
                }
                break;
            case "JOIN":
                String room = inputText[1];
                //checks if the room already exists
                if(roomAndUsers.containsKey(room)) {
                    //checks if the user it's already in the room
                    if(roomAndUsers.containsValue(username)) {
                        respond("ERROR Ya estás dentro de este room", socket);
                    } else {
                        //adds the user to that room
                        roomAndUsers.get(room).add(username);
                    }
                //the room doesn't exist
                }else {
                    if(roomAndUsers.size() < numrooms) {
                        //creates the users list
                        ArrayList<String> users = new ArrayList<>();
                        users.add(username);
                        //creates the room with the user(s)
                        roomAndUsers.put(room, users);
                        System.out.println("CREADO");
                        roomAndUsers.toString();
                    } else {
                        respond("ERROR No existe este room y no se puede crear porque se ha alcanzado el limite de rooms.",socket);
                    }
                }
                break;
            case "LEAVE":
                if(roomAndUsers.containsKey(inputText[1])) {
                    roomAndUsers.get(inputText[1]).remove(username);
                    if(roomAndUsers.get(inputText[1]).isEmpty()) {
                        roomAndUsers.remove(inputText[1]);
                    }
                }
                break;
            case "SEND":
                String[] messageArray = new String[inputText.length-2];
                String messageZ = (new StringBuilder().append(username).append(inputText[1])).toString();
                String messageX = String.join(",", messageArray);
                String message = messageZ + messageX;
                if(roomAndUsers.containsKey(inputText[1])) {
                    ArrayList<Socket> socketsToSend = getSocketsOfClients(roomAndUsers.get(inputText[1]));
                    for(int i = 0; i < socketsToSend.size(); i++) {
                        respond(message,socketsToSend.get(i));
                    }
                } else {
                    respond("ERROR Estas enviando mensaje a un room que no existe.", socket);
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
                    respond("ERROR No estas conectado.", socket);
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
            for(int i = 0; i < roomAndUsers.size(); i++) {
            message = (new StringBuilder().append(" ").append(roomAndUsers.get(i))).toString();
            out.writeUTF(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateClientsInRoom(String room, Socket socket) {
        String message = "USERROOM";
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            ArrayList<String> users = roomAndUsers.get(room);
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
    private boolean userExists(String name) {
        return usersConnected.containsValue(name);
    }
}