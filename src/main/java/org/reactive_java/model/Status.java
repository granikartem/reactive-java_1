package org.reactive_java.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Status {

    WAITING("WAITING", "В ожидании", false),

    IN_PROGRESS("IN_PROGRESS", "В работе", false),

    TESTING("TESTING", "Тестирование", false),

    CLOSED("CLOSED", "Закрыта", true);

    private final String code;

    private final String name;

    private final boolean finalStatus;
}
