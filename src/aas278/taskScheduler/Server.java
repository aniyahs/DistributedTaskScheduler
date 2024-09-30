package aas278.taskScheduler;

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.Collections;
import java.util.logging.Logger;

public class Server {
    private final BlockingQueue<Task> taskQueue;
    private final List<Task> failedTasks;
    private final String id;
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final ExecutorService executor;
    
    //Constructor to initialize task queue and failed tasks 
    public Server(String id){
        Objects.requireNonNull(id, "Server ID cannot be null");
        if (id.isEmpty()){
            throw new IllegalArgumentException("Server ID cannot be null");
        }
        this.id = id;
        this.taskQueue = new LinkedBlockingQueue<>();
        this.failedTasks = new ArrayList<>();
        this.executor = Executors.newCachedThreadPool();
        logger.info("A server has been created: " + id);
     }

     //Adds a task to the server's queue
     public void addTask(Task task) throws ServerException{
        Objects.requireNonNull(task, "Task cannot be null");
        try {
            taskQueue.put(task); // Add task to the queue
            logger.info(task.getId() + "has been aded to" + id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            logger.severe("Failed to add the task to the server: " + e.getMessage());
            throw new ServerException("Failed to add task to server queue", e);
        }
     }

    //Executes all tasks in the queue and returns a list of the completed tasks
    public List<Task> executeTasks() {
        logger.info("Tasks on server " + id + " are being executed");
    
        return taskQueue.stream()
            .map(task -> {
                Future<?> future = null; //Declare future varable
                try {
                    // Wrap task execution to handle TaskException
                    future = executor.submit(() -> {
                        try {
                            task.execute(); // Call the execute method
                        } catch (TaskException e) {
                            throw new RuntimeException(e); // Wrap TaskException in RuntimeException
                        }
                    });
    
                    // Attempt to complete the task within its timeout period
                    future.get(task.getTimeout(), TimeUnit.MILLISECONDS);
    
                    //Check to see if the task was completed 
                    if (task.isCompleted()) {
                        logger.info("Task completed: " + task.getId());
                        return task;
                    } else {
                        throw new RuntimeException("The task has not been completed: " + task.getId());
                    }
    
                } catch (TimeoutException e) {
                    // Timeout occurred, cancel the task and mark it as failed
                    if (future != null) future.cancel(true); // Interrupt the task if it is still running
                    failedTasks.add(task);
                    logger.warning("Task " + task.getId() + " failed on server " + id + " due to timeout");
                    return null;
    
                } catch (InterruptedException | ExecutionException | RuntimeException e) {
                    // Task execution failed, was interrupted, or another runtime exception occurred
                    failedTasks.add(task);
                    logger.warning("Task " + task.getId() + " failed on server " + id + " due to: " + e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull) // Filter out failed tasks
            .collect(Collectors.toList()); // Return successful tasks
    }  

    //Returns a list of failed tasks, uses Collections for an unmodifiable view and a defensive copy
    public List<Task> getFailedTasks(){
        return Collections.unmodifiableList(new ArrayList<>(failedTasks));
    }

    public String getId() {
        return id;
    }
}