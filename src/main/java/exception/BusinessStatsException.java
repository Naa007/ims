package exception;

public class BusinessStatsException extends RuntimeException {

    public BusinessStatsException(String message) {
        super(message);
    }

    public BusinessStatsException(String message, Throwable cause) {
        super(message, cause);
    }
}