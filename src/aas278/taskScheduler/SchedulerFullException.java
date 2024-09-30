package aas278.taskScheduler;

public class SchedulerFullException extends Exception{
    public SchedulerFullException(String message){
        super(message);
    }

    public SchedulerFullException(String message, Throwable cause){
        super(message, cause);
    }
}

