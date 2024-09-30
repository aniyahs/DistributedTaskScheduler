package aas278.taskScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.logging.Logger;

/*
 * PerformanceMonitor tracks and analyzes system performance using streams. 
 * It tracks metrics such as task execution time, success rate, and server utilization. 
 */
public class PerformanceMonitor {

    // These lists hold task metrics for tracking and analysis
    private final List<Long> taskExecutionTimes;
    private final List<Boolean> taskSuccesses;
    private final List<Server> monitoredServers; // Keeps track of the servers
    private static final Logger logger = Logger.getLogger(PerformanceMonitor.class.getName());

    // Thresholds for alerts
    private static final double SUCCESS_RATE_THRESHOLD = 0.8;  // 80% success rate
    private static final double UTILIZATION_THRESHOLD = 0.75;   // 75% server utilization

    public PerformanceMonitor() {
        this.taskExecutionTimes = new ArrayList<>();
        this.taskSuccesses = new ArrayList<>();
        this.monitoredServers = new ArrayList<>();
    }

    // Update the monitor after a task is executed
    public void updateTaskMetrics(Task task, long executionTime, boolean success) {
        taskExecutionTimes.add(executionTime);
        taskSuccesses.add(success);
        logger.info("Task metrics updated: " + task.getId());
    }

    // Calculate average task execution time using streams
    public double getAverageTaskExecutionTime() {
        OptionalDouble averageExecutionTime = taskExecutionTimes.stream()
            .mapToLong(Long::longValue)
            .average();

        return averageExecutionTime.orElse(0);  
    }

    // Calculate success rate using streams
    public double getSuccessRate() {
        long totalTasks = taskSuccesses.size();
        
        if (totalTasks == 0) {
            return 0;
        }

        long successfulTasks = taskSuccesses.stream()
            .filter(success -> success)
            .count();

        return (double) successfulTasks / totalTasks;
    }

    // Calculate server utilization using streams
    public double getServerUtilization(List<Server> servers) {
        long activeServers = servers.stream()
            .filter(server -> !server.getFailedTasks().isEmpty() || !server.executeTasks().isEmpty())
            .count();

        if (monitoredServers.isEmpty()) {
            return 0;
        }
        return (double) activeServers / monitoredServers.size();            
    }

    // Method to get performance statistics
    public String getPerformanceStats(List<Server> servers) {
        double averageExecutionTime = getAverageTaskExecutionTime();
        double successRate = getSuccessRate();
        double utilization = getServerUtilization(servers);

        return String.format("Average Execution Time: %.2f ms\n" +
                             "Success Rate: %.2f%%\n" +
                             "Server Utilization: %.2f%%",
                             averageExecutionTime,
                             successRate * 100,
                             utilization * 100);
    }

    // Trigger alerts if performance thresholds are exceeded
    public void checkForAlerts(List<Server> servers) {
        double successRate = getSuccessRate();
        double utilization = getServerUtilization(servers);

        if (successRate < SUCCESS_RATE_THRESHOLD) {
            logger.warning("Success rate is below the threshold: " + (successRate * 100) + "%");
        }

        if (utilization > UTILIZATION_THRESHOLD) {
            logger.warning("Server utilization has exceeded the threshold: " + (utilization * 100) + "%");
        }
    }

    // Add a new server to the monitor
    public void addServer(Server server) {
        monitoredServers.add(server);
        logger.info("A server has been added, total servers: " + monitoredServers.size());
    }

    // Reset the performance monitor
    public void resetMonitor() {
        taskExecutionTimes.clear();
        taskSuccesses.clear();
        monitoredServers.clear();
        logger.info("Performance monitor has been reset.");
    }
}
