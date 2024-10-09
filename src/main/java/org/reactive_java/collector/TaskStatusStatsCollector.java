package org.reactive_java.collector;

import org.reactive_java.model.Task;
import org.reactive_java.model.TaskStatus;

import java.time.Duration;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

public class TaskStatusStatsCollector implements Collector<Task, Map<Task, Map<TaskStatus, Duration>>, Map<Task, Map<TaskStatus, Duration>>> {
    public static TaskStatusStatsCollector toTaskStatusStatsMap() {
        return new TaskStatusStatsCollector();
    }

    @Override
    public Supplier<Map<Task, Map<TaskStatus, Duration>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Task, Map<TaskStatus, Duration>>, Task> accumulator() {
        return (map, task) -> {
            Map<TaskStatus, Duration> taskStats = new HashMap<>();
            List<TaskStatus> statuses = task.getStatuses();

            for (int i = 0; i < statuses.size() - 1; i++) {
                taskStats.put(statuses.get(i), Duration.between(statuses.get(i).startTime(), statuses.get(i + 1).startTime()));
            }

            map.putIfAbsent(task, taskStats);
        };
    }

    @Override
    public BinaryOperator<Map<Task, Map<TaskStatus, Duration>>> combiner() {
        return (map1, map2) -> {
            for (Map.Entry<Task, Map<TaskStatus, Duration>> entry : map2.entrySet()) {
                map1.putIfAbsent(entry.getKey(), entry.getValue());
            }
            return map1;
        };
    }

    @Override
    public Function<Map<Task, Map<TaskStatus, Duration>>, Map<Task, Map<TaskStatus, Duration>>> finisher() {
        return Collections::unmodifiableMap;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED, IDENTITY_FINISH);
    }
}
