package domain;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Data
@Builder
public class Task {

    private final long taskId;

    private final String taskNumber;

    private final LocalDateTime createTime;

    private final TaskPriority priority;

    private final List<TaskStatus> statuses;

    private User user;

    private String description;
}