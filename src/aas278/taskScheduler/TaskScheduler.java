package aas278.taskScheduler; 

import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects; 
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.logging.Logger;

/*To include priority-based scheduling: 
 * add priority task queue, changed to priorityBlockingQueue to make it thread-safe
 * sort the tasks by their priority
 * assign the tasks to the appopriate server 
 */

 /*To implement load balancing algorithm:
  * Use a map, taskCount, to track # of tasks to be assigned to each server 
  * Use load balancing to evenly distribute tasks across the servers, find least loded server
  * Update executeAll to handle the execute on each server while updating the task count
  */

/*
 * To implement concurrency control:
 * Change PriorityQueue to PriorityBlockingQueue
 * Change map to ConcurrentHashMap to ensure it is thread-safe
 * Utilize parallel streams
 */
public class TaskScheduler {
    private final ArrayList<Server> servers; 
    private final PriorityBlockingQueue<Task> taskQueue;
    private final ConcurrentHashMap<Server, Integer> taskCount;
    private static final int MAX_TASKS_PER_SERVER = 10;
    private final RetryPolicy retryPolicy;
    private static final Logger logger = Logger.getLogger(TaskScheduler.class.getName());

    public TaskScheduler(RetryPolicy retryPolicy) {
        this.servers = new ArrayList<>();
        this.taskQueue = new PriorityBlockingQueue<>(15,Comparator.comparing(Task::getPriority).reversed());         //Initialize with comparator so the tasks can be sorted properly
        this.taskCount = new ConcurrentHashMap<>();
        this.retryPolicy = Objects.requireNonNull(retryPolicy, "Retry policy cannot be null");
    }

    public void addServer(Server server){
        Objects.requireNonNull(server, "Server cannot be null");
        this.servers.add(server);
        taskCount.put(server, 0); //initialize count for the new server, 0
        logger.info("Added server: " + server.getId());
    }

    //Schedules task by assigning it to a server using the hash-based approach
    public void scheduleTask(Task task) throws SchedulerFullException, SchedulerException {
        Objects.requireNonNull(task, "Task cannot be null");
        if (servers.isEmpty()) {
            logger.info("There are no availible servers");
            throw new SchedulerException("No servers available for task scheduling");
        }

        // Check if any server can accept the task
        boolean canSchedule = servers.stream()
            .anyMatch(server -> taskCount.get(server) < MAX_TASKS_PER_SERVER);

        if (!canSchedule) {
            throw new SchedulerFullException("All servers are at full capacity");
        }

        // Add the task to the priority queue
        taskQueue.add(task.createCopy());
        logger.info("The following task has been scheduled: " + task.getId());
    }
    
    /* 
     * Executes tasks on all servers and returns a map of completed tasks per server
     * Use parallel streams to make it concurrent
     */
    public Map<Server, List<Task>> executeAll() throws SchedulerException, SchedulerFullException {
        Map<Server, List<Task>> completedTasks = new ConcurrentHashMap<>();

        // Loop through servers to assign and execute tasks based on priority and load
        while (!taskQueue.isEmpty()) {
            Task task = taskQueue.poll(); // Get the highest priority task
            if (task != null) {
                // Find the server with the least number of assigned tasks
                Server selectedServer = taskCount.entrySet().stream()
                        .min(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElseThrow(() -> new SchedulerFullException("No available server"));

                // Add the task to the server and update the task count
                try {
                    //selectedServer.addTask(task);
                    attemptTaskExecution(selectedServer, task); //Instead of adding a task to a selected Server we refer to the new method that considers our retry method
                    taskCount.put(selectedServer, taskCount.get(selectedServer) + 1);
                    logger.info("A task has been successfully executed: " + task.getId());
                } catch (ServerException e) {
                    throw new SchedulerException("Error adding task to server: " + selectedServer, e);
                }
            }  
        }

        /* Execute tasks on the server 
        * Work on a copy to ensure defensive copying
        * Use parallel streams to ensure concurrent programming
        */
        servers.parallelStream().forEach(server -> {
                List<Task> completed = server.executeTasks();
                completedTasks.putIfAbsent(server, new ArrayList<>());
                completedTasks.get(server).addAll(completed);
                logger.info("Executed tasks on server: " + server.getId());
            });
        //Collections used for defensive copying, allows for external access while ensuring no modifications
        return Collections.unmodifiableMap(completedTasks);
    }

    /**
     * Attempt to execute a task on a server, applying the retry mechanism on failure.
     * Logs appropriate retry information and retries based on the RetryPolicy.
     */
    private void attemptTaskExecution(Server server, Task task) throws ServerException {
        int attempt = 0;
        boolean success = false;

        //Task execution is attempted until max number of tries is exceeded / the task has been successfully executed
        while (attempt <= retryPolicy.getMaxRetries() && !success) {
            try {
                logger.info("Executing task: " + task.getId() + " on server: " + server.getId() + " (Attempt " + (attempt + 1) + ")");
                server.addTask(task);
                success = true;  // Task successfully added to the server
            } catch (ServerException e) {
                //If the task cannot be added successfully then do the folllowing 
                attempt++;
                if (attempt <= retryPolicy.getMaxRetries()) {
                    long delay = retryPolicy.getDelay(attempt); //get the proper delay time
                    logger.warning("Task execution failed: " + task.getId() + " (Attempt " + attempt + ")");
                    logger.info("Retrying in " + delay + " milliseconds...");
                    retryPolicy.waitForRetry(attempt);  // wait before retrying
                } else {
                    logger.severe("Task execution failed permanently: " + task.getId() + " after " + attempt + " attempts.");
                    throw new ServerException("Max retry attempts reached for task: " + task.getId(), e);
                }
            }
        }
    }

    public synchronized List<Server> getServers() {
        return Collections.unmodifiableList(new ArrayList<>(servers));
    }

    public synchronized Map<Server, Integer> getTaskCount() {
        return Collections.unmodifiableMap(new HashMap<>(taskCount));
    }
}