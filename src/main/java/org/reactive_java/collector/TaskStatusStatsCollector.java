package org.reactive_java.collector;

import org.reactive_java.model.Evaluation;
import org.reactive_java.model.Task;
import org.reactive_java.model.User;

import java.time.Duration;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

public class TaskStatusStatsCollector implements Collector<Task, Map<User, Map<Task, Boolean>>, Map<User, Map<Task, Boolean>>> {
    public static TaskStatusStatsCollector toTaskStatusStatsMap() {
        return new TaskStatusStatsCollector();
    }

    @Override
    public Supplier<Map<User, Map<Task, Boolean>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<User, Map<Task, Boolean>>, Task> accumulator() {
        return (map, task) -> {
            User user = task.getUser();
            Evaluation evaluation = task.getEvaluation();
            boolean taskCompletedOnTime = task.getStatuses()
                    .stream()
                    .noneMatch(
                            taskStatus -> Duration.between(taskStatus.startTime(), taskStatus.finishTime())
                            .compareTo(evaluation.getStatusDurationMap().get(taskStatus.status())) > 0
            );

//            Iterator<TaskStatus> taskStatusIterator = task.getStatuses().iterator();
//
//            while (taskStatusIterator.hasNext()) {
//                TaskStatus taskStatus = taskStatusIterator.next();
//                taskCompletedOnTime &= Duration.between(taskStatus.startTime(), taskStatus.finishTime()).compareTo(evaluation.getStatusDurationMap().get(taskStatus.status())) <= 0;
//            }


            if (map.containsKey(user)) {
                map.get(user).put(task, taskCompletedOnTime);
            } else {
                map.put(user, Map.of(task, taskCompletedOnTime));
            }
        };
    }

    @Override
    public BinaryOperator<Map<User, Map<Task, Boolean>>> combiner() {
        return (map1, map2) -> {
            map2.forEach((user, value) -> {
                if (map1.containsKey(user)) {
                    map1.get(user).putAll(value);
                } else {
                    map1.put(user, value);
                }
            });
//            Iterator<Map.Entry<User, Map<Task, Boolean>>> map2Iterator = map2.entrySet().iterator();
//            while (map2Iterator.hasNext()) {
//                Map.Entry<User, Map<Task, Boolean>> entry = map2Iterator.next();
//                User user = entry.getKey();
//                if (map1.containsKey(user)) {
//                    map1.get(user).putAll(entry.getValue());
//                } else {
//                    map1.put(user, entry.getValue());
//                }
//            }
            return map1;
        };
    }

    @Override
    public Function<Map<User, Map<Task, Boolean>>, Map<User, Map<Task, Boolean>>> finisher() {
        return Collections::unmodifiableMap;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED, IDENTITY_FINISH);
    }
}
