
package futurepath.chat.threads;

import futurepath.chat.server.controller.CommandsController;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carolina da Silva
 */
public class ClientConnetion extends Thread{
    private CommandsController command;
    private Socket clientSocket;
    private DataInputStream in;
    

    public ClientConnetion(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    
    @Override
    public void run() {
                
        while(true){
            try {
                command = new CommandsController();
                in = new DataInputStream(clientSocket.getInputStream());
                String message = in.readUTF();
                if(!message.isEmpty()){
                    String[] arrayComands = message.split(" ");
                    command.interpret(arrayComands, clientSocket);
                }
            } catch (IOException ex) {
                ;
            }             
        }
    }
       
    
    
}
