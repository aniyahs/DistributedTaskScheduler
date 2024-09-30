package aas278.taskScheduler;

import java.math.BigInteger;
import java.util.Objects;

public final class Duration {
    private final BigInteger milliseconds;

    // Private constructor to ensure immutability
    private Duration(BigInteger milliseconds) {
        this.milliseconds = milliseconds;
    }

    // Static factory method to create a Duration from milliseconds
    public static Duration ofMillis(long millis) {
        assert millis != 0 : "Milliseconds cannot be 0"; //null check for primitive
        return new Duration(BigInteger.valueOf(millis));
    }

    // Method to add two Duration instances
    public Duration add(Duration other) {
        Objects.requireNonNull(other, "Duration to add cannot be null"); //null check for object
        return new Duration(this.milliseconds.add(other.milliseconds));
    }

    // Method to subtract two Duration instances
    public Duration subtract(Duration other) {
        Objects.requireNonNull(other, "Duration to subtract cannot be null");
        return new Duration(this.milliseconds.subtract(other.milliseconds));
    }

    // Method to compare two Duration instances
    public int compareTo(Duration other) {
        Objects.requireNonNull(other, "Duration to subtract cannot be null");
        return this.milliseconds.compareTo(other.milliseconds);
    }

    // Method to get the duration in milliseconds as a long
    public long toMillis() {
        return milliseconds.longValue();
    }

    @Override
    public String toString() {
        return milliseconds.toString() + " ms";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Duration duration = (Duration) o;
        return milliseconds.equals(duration.milliseconds);
    }

    @Override
    public int hashCode() {
        return milliseconds.hashCode();
    }
}
