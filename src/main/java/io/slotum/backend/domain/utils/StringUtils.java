package io.slotum.backend.domain.utils;

public final class StringUtils {
    private StringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Нормализует строковое значение, обрезая пробелы по краям.
     * Если аргумент равен {@code null} или состоит только из пробелов,
     * возвращается {@code null}.
     *
     * @param  value строка для нормализации, может быть {@code null}
     * @return обрезанная строка, или {@code null}, если аргумент
     *         был {@code null} или пустым после обрезки
     */
    public static String normalize(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();
        return value.isEmpty() ? null : value;
    }
}
