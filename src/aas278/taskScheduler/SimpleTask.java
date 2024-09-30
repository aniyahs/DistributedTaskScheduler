package aas278.taskScheduler;

import java.util.Objects;
import java.util.Set;

public final class SimpleTask implements Task {
    private final String id;
    private boolean isCompleted; 
    private final Duration estimatedDuration; 
    private final TaskPriority priority;

    // Constructor, with initializations to ensure immutability
    public SimpleTask(String id, Duration estimatedDuration, TaskPriority priority) {
        Objects.requireNonNull(id, "Task ID cannot be null");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("The Task ID cannot be empty");
        }

        this.id = id; 
        this.estimatedDuration = Objects.requireNonNull(estimatedDuration, "Duration cannot be null");
        this.isCompleted = false;
        this.priority = priority;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void execute() throws TaskException {
        try {
            // Simulate task execution by waiting for the estimated duration
            Thread.sleep(estimatedDuration.toMillis());
            isCompleted = true; // Mark task as completed after execution
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TaskException("Task execution interrupted", e);
        }
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }

    @Override
    public String toString() {
        return "SimpleTask{id='" + id + "', estimatedDuration=" + estimatedDuration + ", completed=" + isCompleted + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleTask that = (SimpleTask) o;
        return isCompleted == that.isCompleted &&
                id.equals(that.id) &&
                priority == that.priority &&
                estimatedDuration.equals(that.estimatedDuration);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + estimatedDuration.hashCode();
        result = 31 * result + (isCompleted ? 1 : 0);
        result = 31 * result + priority.hashCode();
        return result;
    }

    @Override
    public TaskPriority getPriority() {
        return priority;
    }

    @Override 
    public Task createCopy() {
        return new SimpleTask(this.id, this.estimatedDuration, this.priority);
    }

    @Override
    public Set<String> getDependencies() {
        throw new UnsupportedOperationException("Unimplemented method 'getDependencies'");
    }

    @Override
    public long getTimeout() {
        throw new UnsupportedOperationException("Unimplemented method 'getTimeout'");
    }
}
