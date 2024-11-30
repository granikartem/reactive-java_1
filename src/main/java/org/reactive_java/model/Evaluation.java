package org.reactive_java.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@Data
@Builder
public class Evaluation {
    private final Duration estimatedCompletionTime;

    private final Map<Status, Duration> statusDurationMap;

    private final User evaluatedBy;
}
