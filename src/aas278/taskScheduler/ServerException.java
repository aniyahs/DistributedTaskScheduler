package aas278.taskScheduler;

public class ServerException extends Exception {
    // Constructor with a message
    public ServerException(String message) {
        super(message);
    }

    // Constructor with a message and a cause
    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
