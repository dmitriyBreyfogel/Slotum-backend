package io.slotum.backend.error;

import java.util.Map;

/**
 * Класс серверной ошибки
 */
public final class AppException extends RuntimeException {
    private final ErrorCode code;
    private final Map<String, Object> details;

    private AppException(ErrorCode code, String logMessage, Map<String, Object> details) {
        super(logMessage);
        this.code = code;
        this.details = (details == null) ? Map.of() : Map.copyOf(details);
    }

    /**
     * Сборка серверной ошибки
     * @param code код ошибки
     * @param logMessage сообщение ошибки
     * @param details детали ошибки
     * @return собранная по данным параметрам серверная ошибка
     */
    public static AppException build(ErrorCode code, String logMessage, Map<String, Object> details) {
        return new AppException(code, logMessage, details);
    }

    /**
     * Сборка серверной ошибки без её деталей
     * @param code код ошибки
     * @param logMessage сообщение ошибки
     * @return собранная по данным параметрам серверная ошибка
     */
    public static AppException build(ErrorCode code, String logMessage) {
        return new AppException(code, logMessage, null);
    }

    /**
     * Сборка серверной ошибки без собщения и её деталей
     * @param code код ошибки
     * @return собранная по данным параметрам серверная ошибка
     */
    public static AppException build(ErrorCode code) {
        return new AppException(code, null, null);
    }


    /* Getters */
    public ErrorCode getCode() {
        return code;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}