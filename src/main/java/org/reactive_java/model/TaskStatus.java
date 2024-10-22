package org.reactive_java.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TaskStatus(Status status, LocalDateTime startTime, LocalDateTime finishTime) {

}
