
package futurepath.chat.server.exceptions;


public class numberOfUsersTooSmallException extends Exception{

    public numberOfUsersTooSmallException() {
        super("The number of users per rooms is too small. Select a number between 2 and 50");
    }

}
