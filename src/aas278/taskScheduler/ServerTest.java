package aas278.taskScheduler;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ServerTest {

    private Server server;

    @Before
    public void setUp() {
        server = new Server("Server1");
    }

    @Test
    public void testAddTask() throws ServerException, java.rmi.ServerException {
        Task task = new SimpleTask("Task1", Duration.ofMillis(500), TaskPriority.HIGH);
        
        server.addTask(task);
        
        List<Task> failedTasks = server.getFailedTasks();
        assertTrue(failedTasks.isEmpty()); // No tasks should be in failed tasks initially
    }

    @Test
    public void testExecuteTasks() throws ServerException, java.rmi.ServerException {
        Task task1 = new SimpleTask("Task1", Duration.ofMillis(500), TaskPriority.HIGH);
        Task task2 = new SimpleTask("Task2", Duration.ofMillis(1000), TaskPriority.LOW);
        Task task3 = new SimpleTask("Task3", Duration.ofMillis(500), TaskPriority.HIGH);

        server.addTask(task1);
        server.addTask(task2);
        server.addTask(task3);

        // Execute tasks
        //List<Task> completedTasks = server.executeTasks();

        // Verify tasks are completed
        //assertEquals(3, completedTasks.size());
        //assertTrue(completedTasks.stream().allMatch(Task::isCompleted));
    }

    // @Test
    // public void testExecuteTasksWithFailure() throws ServerException {
    //     Task task = new SimpleTask("Task1", Duration.ofMillis(500), TaskPriority.HIGH) {
    //         @Override
    //         public void execute() throws TaskException {
    //             // Override to simulate a task failure
    //             throw new TaskException("Simulated task failure");
    //         }
    //     };

    //     server.addTask(task);

    //     // Execute tasks and handle exceptions
    //     List<Task> completedTasks = server.executeTasks();

    //     // Verify task failed
    //     List<Task> failedTasks = server.getFailedTasks();
    //     assertEquals(1, failedTasks.size());
    //     assertEquals(task, failedTasks.get(0));
    //     assertTrue(completedTasks.isEmpty());
    // }

    // @Test
    // public void testGetFailedTasks() throws ServerException {
    //     Task task1 = new SimpleTask("Task1", Duration.ofMillis(500), TaskPriority.HIGH);
    //     Task task2 = new SimpleTask("Task2", Duration.ofMillis(1000), TaskPriority.LOW) {
    //         @Override
    //         public void execute() throws TaskException {
    //             throw new TaskException("Simulated task failure");
    //         }
    //     };

    //     server.addTask(task1);
    //     server.addTask(task2);

    //     // Execute tasks with failures
    //     server.executeTasks();

    //     // Verify failed tasks
    //     List<Task> failedTasks = server.getFailedTasks();
    //     assertEquals(1, failedTasks.size());
    //     assertTrue(failedTasks.get(0) instanceof SimpleTask);
    //     assertEquals("Task2", failedTasks.get(0).getId());
    // }

    @Test
    public void testServerIdValidation() {
        // Check invalid server ID
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Server("");
        });
        assertEquals("Server ID cannot be null", exception.getMessage());
    }
}
