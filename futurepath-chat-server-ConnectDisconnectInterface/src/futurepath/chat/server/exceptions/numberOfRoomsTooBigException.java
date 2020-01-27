
package futurepath.chat.server.exceptions;


public class numberOfRoomsTooBigException extends Exception{

    public numberOfRoomsTooBigException() {
        super("The number of rooms is too big. Select a number between 1 and 20");
    }
    
}
