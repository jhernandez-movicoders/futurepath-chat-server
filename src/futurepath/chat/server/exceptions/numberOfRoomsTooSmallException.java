
package futurepath.chat.server.exceptions;


public class numberOfRoomsTooSmallException extends Exception{

    public numberOfRoomsTooSmallException() {
        super("The number of rooms is too small. Select a number between 1 and 20");
    }

}
