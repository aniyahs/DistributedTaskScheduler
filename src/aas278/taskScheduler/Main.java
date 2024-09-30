package aas278.taskScheduler;

import java.util.List;
import java.util.Map;

/* Currently Main demonstrates the folllowing:
 * RetryPolicy with 3 max retries and a 5 second delay (exponential backoff is disabled)
 * Two servers are created 
 * Servers are added to the scheduler 
 * SimpleTask instances are created 
 * The simple tasks are wrapped in priority tasks to give them a priority 
 * ExecuteAll from TaskScheduler is called to run all of the tasks concurrently 
 * Results should be shown as well as logging 
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Create a RetryPolicy instance
            RetryPolicy retryPolicy = new RetryPolicy(3, 5000, false); // 3 retries, 1 second delay

            // Create a TaskScheduler instance with the retry policy
            TaskScheduler taskScheduler = new TaskScheduler(retryPolicy);

            // Create some Server instances
            Server server1 = new Server("Server 1");
            Server server2 = new Server("Server 2");

            // Add servers to the scheduler
            taskScheduler.addServer(server1);
            taskScheduler.addServer(server2);

            // Create SimpleTask instances with different durations
            SimpleTask simpleTask1 = new SimpleTask("Task1", Duration.ofMillis(1000), null);
            SimpleTask simpleTask2 = new SimpleTask("Task2", Duration.ofMillis(2000), null);
            SimpleTask simpleTask3 = new SimpleTask("Task3", Duration.ofMillis(500), null);

            // Create dependent tasks
            //Set<String> dependencies = Set.of(simpleTask1.getId());
            SimpleTask dependentTask = new SimpleTask("DependentTask", Duration.ofMillis(1500), TaskPriority.MEDIUM);

            // Wrap SimpleTask instances into PriorityTask with different priorities
            PriorityTask priorityTask1 = new PriorityTask(simpleTask1, TaskPriority.MEDIUM);
            PriorityTask priorityTask2 = new PriorityTask(simpleTask2, TaskPriority.HIGH);
            PriorityTask priorityTask3 = new PriorityTask(simpleTask3, TaskPriority.LOW);
            PriorityTask priorityDependentTask = new PriorityTask(dependentTask, TaskPriority.MEDIUM);

            // Schedule the tasks
            try {
                taskScheduler.scheduleTask(priorityTask1);
                taskScheduler.scheduleTask(priorityTask2);
                taskScheduler.scheduleTask(priorityTask3);
                taskScheduler.scheduleTask(priorityDependentTask);
                System.out.println("Tasks scheduled successfully.");
            } catch (SchedulerFullException e) {
                System.err.println("Error scheduling tasks: " + e.getMessage());
            }

            // Execute all tasks and retrieve the results
            try {
                Map<Server, List<Task>> completedTasks = taskScheduler.executeAll();

                // Print the results
                for (Map.Entry<Server, List<Task>> entry : completedTasks.entrySet()) {
                    Server server = entry.getKey();
                    List<Task> tasks = entry.getValue();
                    System.out.println("Completed tasks on " + server.getId() + ":");
                    for (Task task : tasks) {
                        System.out.println("  - " + task.getId() + " (Duration: " + task.getEstimatedDuration() + ", Completed: " + task.isCompleted() + ")");
                    }
                }

                // Print the task count per server
                Map<Server, Integer> taskCount = taskScheduler.getTaskCount();
                System.out.println("Task count per server:");
                for (Map.Entry<Server, Integer> entry : taskCount.entrySet()) {
                    System.out.println("  - " + entry.getKey().getId() + ": " + entry.getValue());
                }

            } catch (SchedulerException e) {
                System.err.println("Error executing tasks: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
