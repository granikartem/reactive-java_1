package org.reactive_java.collector;

import org.reactive_java.model.Task;
import org.reactive_java.model.TaskStatus;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static org.reactive_java.util.Constants.MAX_STATUS_DURATION;

public class TaskStatusStatsCollector implements Collector<Task, Map<Task,Map<TaskStatus, Duration>>, Map<Task, Map<TaskStatus, Duration>>> {
    @Override
    public Supplier<Map<Task, Map<TaskStatus, Duration>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Task, Map<TaskStatus, Duration>>, Task> accumulator() {
        return (map, task) -> {
            Map<TaskStatus, Duration> taskStats = new HashMap<>();
            List<TaskStatus> statuses = task.getStatuses();
            if (statuses.size() > 1){
                for (int i = 0; i < statuses.size() - 1; i++) {
                    taskStats.put(statuses.get(i), Duration.between(statuses.get(i).startTime(), statuses.get(i + 1).startTime()));
                }
            }
            taskStats.put(statuses.get(statuses.size() - 1), Duration.ofSeconds(ThreadLocalRandom.current().nextLong(MAX_STATUS_DURATION.toSeconds())));
            map.put(task, taskStats);
        };
    }

    @Override
    public BinaryOperator<Map<Task, Map<TaskStatus, Duration>>> combiner() {
        return (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        };
    }

    @Override
    public Function<Map<Task, Map<TaskStatus, Duration>>, Map<Task, Map<TaskStatus, Duration>>> finisher() {
        return Collections::unmodifiableMap;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }

    public static TaskStatusStatsCollector toTaskStatusStatsMap() {
        return new TaskStatusStatsCollector();
    }
}
