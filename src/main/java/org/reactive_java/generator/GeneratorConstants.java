package org.reactive_java.generator;

import org.reactive_java.model.Status;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public final class GeneratorConstants {
    final static LocalDate START_DATE = LocalDate.of(2024, 1, 1);
    final static LocalDate END_DATE = LocalDate.of(2024, 12, 31);

    final static Duration MAX_STATUS_DURATION = Duration.ofHours(12);

    final static int STATUS_AMOUNT = Status.values().length;

    final static int USER_AMOUNT = 1000;
    final static int MAX_GROUPS_PER_USER = 4;
    final static List<String> USER_GROUPS = List.of(
            "ADMIN",
            "DEVELOPER",
            "DEVOPS",
            "PRODUCT",
            "TESTER",
            "SUPPORT",
            "DESIGNER",
            "DOCUMENTATION",
            "OTHER"
    );
}
