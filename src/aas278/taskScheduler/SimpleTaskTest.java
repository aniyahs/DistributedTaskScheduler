package aas278.taskScheduler;

import static org.junit.Assert.*;
//import org.junit.BeforeEach;
import org.junit.Test;

public class SimpleTaskTest {

    private SimpleTask task;
    private Duration duration;

    @BeforeEach
    public void setUp() {
        duration = Duration.ofMillis(1000); // 1 second
        task = new SimpleTask("task1", duration, TaskPriority.MEDIUM);
    }

    @Test
    public void testConstructor() {
        assertEquals("task1", task.getId());
        assertEquals(duration, task.getEstimatedDuration());
        assertFalse(task.isCompleted());
        assertEquals(TaskPriority.MEDIUM, task.getPriority());
    }

    @Test
    public void testExecute() throws TaskException {
        assertFalse(task.isCompleted());
        task.execute();
        assertTrue(task.isCompleted());
    }

    @Test
    public void testEqualsAndHashCode() {
        SimpleTask sameTask = new SimpleTask("task1", duration, TaskPriority.MEDIUM);
        SimpleTask differentTask = new SimpleTask("task2", duration, TaskPriority.HIGH);

        assertEquals(task, sameTask);
        assertNotEquals(task, differentTask);

        assertEquals(task.hashCode(), sameTask.hashCode());
        assertNotEquals(task.hashCode(), differentTask.hashCode());
    }

    @Test
    public void testCreateCopy() {
        SimpleTask copy = (SimpleTask) task.createCopy();
        assertEquals(task, copy);
        assertNotSame(task, copy); // Ensure it's a different object
    }

    @Test
    public void testConstructorNullId() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new SimpleTask(null, duration, TaskPriority.MEDIUM);
        });
        assertEquals("Task ID cannot be null", thrown.getMessage());
    }

    @Test
    public void testConstructorEmptyId() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new SimpleTask("", duration, TaskPriority.MEDIUM);
        });
        assertEquals("The Task ID cannot be null", thrown.getMessage());
    }

    @Test
    public void testConstructorNullDuration() {
        NullPointerException thrown = assertThrows(NullPointerException.class, () -> {
            new SimpleTask("task1", null, TaskPriority.MEDIUM);
        });
        assertEquals("Duration cannot be null", thrown.getMessage());
    }
}