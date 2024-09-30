package aas278.taskScheduler;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class TaskSchedulerTest {

    private TaskScheduler scheduler;
    private Server server1;
    private Server server2;

    @Before
    public void setUp() {
        //scheduler = new TaskScheduler();
        server1 = new Server("Server1");
        server2 = new Server("Server2");
        scheduler.addServer(server1);
        scheduler.addServer(server2);
    }

    @Test
    public void testAddServer() {
        assertEquals(2, scheduler.getServers().size());
    }

    @Test
    public void testScheduleTask() throws SchedulerFullException, SchedulerException {
        Task task1 = new SimpleTask("Task1", Duration.ofMillis(500), TaskPriority.HIGH);
        Task task2 = new SimpleTask("Task2", Duration.ofMillis(1000), TaskPriority.LOW);

        scheduler.scheduleTask(task1);
        scheduler.scheduleTask(task2);

        assertEquals(2, scheduler.getTaskCount().values().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    public void testExecuteAll() throws SchedulerFullException, SchedulerException {
        Task task1 = new SimpleTask("Task1", Duration.ofMillis(500), TaskPriority.HIGH);
        Task task2 = new SimpleTask("Task2", Duration.ofMillis(1000), TaskPriority.LOW);

        scheduler.scheduleTask(task1);
        scheduler.scheduleTask(task2);

        Map<Server, List<Task>> completedTasks = scheduler.executeAll();

        assertEquals(2, completedTasks.values().stream().flatMap(List::stream).count());
        assertTrue(completedTasks.get(server1).stream().anyMatch(task -> task.getId().equals("Task1") || task.getId().equals("Task2")) ||
                   completedTasks.get(server2).stream().anyMatch(task -> task.getId().equals("Task1") || task.getId().equals("Task2")));
    }

    @Test
    public void testScheduleTaskSchedulerFullException() throws SchedulerFullException, SchedulerException {
        // Fill up the scheduler to capacity
        for (int i = 0; i < 20; i++) {
            scheduler.scheduleTask(new SimpleTask("Task" + i, Duration.ofMillis(500), TaskPriority.LOW));
        }

        Task newTask = new SimpleTask("NewTask", Duration.ofMillis(500), TaskPriority.LOW);
        SchedulerFullException exception = assertThrows(SchedulerFullException.class, () -> {
            scheduler.scheduleTask(newTask);
        });

        assertEquals("All servers are at full capacity", exception.getMessage());
    }

    @Test
    public void testSchedulerExceptionWhenNoServers() {
        TaskScheduler emptyScheduler = new TaskScheduler(null);
        Task task = new SimpleTask("Task1", Duration.ofMillis(500), TaskPriority.HIGH);

        SchedulerException exception = assertThrows(SchedulerException.class, () -> {
            emptyScheduler.scheduleTask(task);
        });

        assertEquals("No servers available for task scheduling", exception.getMessage());
    }
}
