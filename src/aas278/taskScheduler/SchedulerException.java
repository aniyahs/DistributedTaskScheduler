package aas278.taskScheduler;

public class SchedulerException extends Exception {
    // Constructor with a message
    public SchedulerException(String message) {
        super(message);
    }

    // Constructor with a message and a cause
    public SchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}
