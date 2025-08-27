package co.com.crediya.config.adapters;

import co.com.crediya.model.user.gateways.LoggerPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Slf4jLoggerAdapter implements LoggerPort {

    @Override
    public void info(String message, Object... args) {
        log.info(message, args);
    }

    @Override
    public void error(String message, Throwable error) {
        log.error(message, error);
    }
}