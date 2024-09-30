package aas278.taskScheduler;

import java.util.Set;
import java.util.Objects;
import java.util.Collections;

public final class DependentTask extends PriorityTask{
    private final Set<String> dependencies;

    /*
     * Set<String> dependencies is passed, which contains the IDs of the tasks that must be completed before this task
     */
    public DependentTask(Task task, TaskPriority priority, Set<String> dependencies) {
        super(task, priority);
        Objects.requireNonNull(dependencies, "Dependiencies cannot be null");
        this.dependencies = Collections.unmodifiableSet(dependencies); // immutable by using Collections
    }

    @Override
    public Set<String> getDependencies() {
        return dependencies;
    }

    //Utilize createCopy to ensure that dependencies are kept even when a task copy is created
    @Override
    public Task createCopy() {
        return new DependentTask(super.createCopy(), getPriority(), dependencies);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DependentTask)) return false;
        if (!super.equals(o)) return false;
        DependentTask that = (DependentTask) o;
        return dependencies.equals(that.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dependencies);
    }

    @Override
    public String toString() {
        return "DependentTask{id='" + getId() + "', estimatedDuration=" + getEstimatedDuration() + 
               ", priority=" + getPriority() + ", dependencies=" + dependencies + '}';
    }
}
