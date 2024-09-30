package aas278.taskScheduler;

import java.util.Set;

public interface Task {
    
    //Returns unique identifier for the task
    String getId();

    //Executes the task 
    void execute() throws TaskException;

    //Returns the completion status
    boolean isCompleted();

    //Returns estimated duration of task 
    Duration getEstimatedDuration();

    //Method to return the priority of a task; see TaskPriority
    TaskPriority getPriority();

    //Factory method to create a copy of the task in TaskScheduler, all tasks are copyable
    Task createCopy();

    //Method to return task IDs this task depends on 
    Set<String> getDependencies();

    //Method to get the task timeout
    long getTimeout();
}

