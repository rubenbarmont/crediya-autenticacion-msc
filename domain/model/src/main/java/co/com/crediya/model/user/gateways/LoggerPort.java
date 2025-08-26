package co.com.crediya.model.user.gateways;

public interface LoggerPort {
    void info(String message, Object... args);
    void error(String message, Throwable error);
}
