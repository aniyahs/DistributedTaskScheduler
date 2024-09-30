package aas278.taskScheduler;

import java.util.concurrent.TimeUnit;

public class RetryPolicy {
    private final int maxRetries;
    private final long delay;
    private final boolean exponentialBackoff;


    //instead of boolean can use lambda function to determine the exponential backoff 

    public RetryPolicy(int maxRetries, long delay, boolean exponentialBackoff) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries cannot be negative");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("Delay cannot be negative");
        }
        this.maxRetries = maxRetries;
        this.delay = delay;
        this.exponentialBackoff = exponentialBackoff;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    /*
     * Determines how long to wait between retry attempts depending on the status of exponentialBackooff (true/false)
     * Also takes into account the attempt number of the retry attempt, updating the delay
     */
    public long getDelay(int attempt) {
        if (exponentialBackoff) {
            return delay * (1L << (attempt - 1)); // Exponential backoff calculation
        }
        return delay;
    }

    /*
     * Uses getDelay to put the thread to sleep for however long the duration has been calculated to be
     */
    public void waitForRetry(int attempt) {
        try {
            long waitTime = getDelay(attempt);
            System.out.println("Waiting " + waitTime + " milliseconds before retry...");
            TimeUnit.MILLISECONDS.sleep(waitTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Retry interrupted: " + e.getMessage());
        }
    }
}
