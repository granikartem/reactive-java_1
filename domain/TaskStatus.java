package domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TaskStatus(Status status, LocalDateTime startTime) {

}
