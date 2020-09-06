package br.com.training.threads.messaging.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    public void uncaughtException(Thread t, Throwable e) {
        LOGGER.error("Unexpected Exception: {}", e.getMessage());
    }
}
