package aas278.taskScheduler;
import java.util.Objects; 
import java.util.Set; 

public class PriorityTask implements Task {
    private final Task simpleTask;
    private final TaskPriority priority;

    public PriorityTask(Task task, TaskPriority priority) {
        this.simpleTask = Objects.requireNonNull(task, "SimpleTask cannot have null value");
        this.priority = Objects.requireNonNull(priority, "Task priority cannot be null");
    }

    @Override
    public String getId() {
        return simpleTask.getId();
    }

    @Override
    public void execute() throws TaskException {
        simpleTask.execute();
    }

    @Override
    public boolean isCompleted() {
        return simpleTask.isCompleted();
    }

    @Override
    public Duration getEstimatedDuration() {
        return simpleTask.getEstimatedDuration();
    }

    @Override
    public TaskPriority getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "PriorityTask{simpleTask=" + simpleTask + ", priority=" + priority + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriorityTask that = (PriorityTask) o;
        return simpleTask.equals(that.simpleTask) && priority == that.priority;
    }

    @Override
    public int hashCode() {
        int result = simpleTask.hashCode();
        result = 31 * result + priority.hashCode();
        return result;
    }

    @Override
    public Task createCopy() {
       return new PriorityTask(simpleTask.createCopy(), priority);
    }

    //Return an empty set since there are no dependencies 
    @Override
    public Set<String> getDependencies() {
        return Set.of(); 
    }

    @Override
    public long getTimeout() {
        throw new UnsupportedOperationException("Unimplemented method 'getTimeout'");
    }
}