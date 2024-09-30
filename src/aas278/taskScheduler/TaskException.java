package aas278.taskScheduler;

public class TaskException extends Exception {
    // Constructor with a message
    public TaskException(String message) {
        super(message);
    }

    // Constructor with a message and a cause
    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
