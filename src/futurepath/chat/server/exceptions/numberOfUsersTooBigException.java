
package futurepath.chat.server.exceptions;


public class numberOfUsersTooBigException extends Exception{

    public numberOfUsersTooBigException() {
        super("The number of users per rooms is too big. Select a number between 2 and 50");
    }
    
}
