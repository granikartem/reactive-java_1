package org.reactive_java;

import org.reactive_java.collector.TaskStatusStatsCollector;
import org.reactive_java.model.Task;
import org.reactive_java.model.TaskStatus;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.reactive_java.generator.TaskGenerator.generateTasks;


public class Main {

    public static void main(String[] args) {

        var tasks = generateTasks(5000);
        System.out.println("Для коллекции с 5000 элементов:\n");
        printWorkTime(tasks);

        tasks = generateTasks(50000);
        System.out.println("\nДля коллекции с 50000 элементов:\n");
        printWorkTime(tasks);

        tasks = generateTasks(250000);
        System.out.println("\nДля коллекции с 250000 элементов:\n");
        printWorkTime(tasks);
    }

    private static void printWorkTime(List<Task> tasks) {
        System.out.println(MessageFormat.format("Время работы итерационным циклом по коллекции = {0}\n",
                getCollectorWorkTimeInMillis(Main::getTaskStatusesDurationMapByIterator, tasks)));
        System.out.println(MessageFormat.format("Время работы конвейером с помощью Stream API = {0}\n",
                getCollectorWorkTimeInMillis(Main::getTaskStatusesDurationMapByStream, tasks)));
        System.out.println(MessageFormat.format("Время работы конвейером с помощью собственного коллектора = {0}\n",
                getCollectorWorkTimeInMillis(Main::getTaskStatusesDurationMapByCollector, tasks)));
    }

    private static double getCollectorWorkTimeInMillis(Function<List<Task>, Map<Task, Map<TaskStatus, Duration>>> collector,
                                                       List<Task> tasks) {
        List<Duration> durations = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Instant startTime = Instant.now();
            collector.apply(tasks);
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            durations.add(duration);
        }

        return durations.stream().mapToLong(Duration::toMillis).average().getAsDouble();
    }

    private static Map<Task, Map<TaskStatus, Duration>> getTaskStatusesDurationMapByIterator(List<Task> tasks) {
        var taskStatusDurationMap = new HashMap<Task, Map<TaskStatus, Duration>>();
        for (Task task : tasks) {
            var statusDurationMap = new HashMap<TaskStatus, Duration>();
            for (int i = 0; i < task.getStatuses().size() - 1; i++) {
                statusDurationMap.put(task.getStatuses().get(i), Duration.between(task.getStatuses().get(i).startTime(),
                        task.getStatuses().get(i + 1).startTime()));
            }
            taskStatusDurationMap.put(task, statusDurationMap);
        }
        return taskStatusDurationMap;
    }

    private static Map<Task, Map<TaskStatus, Duration>> getTaskStatusesDurationMapByStream(List<Task> tasks) {
        return tasks.stream().collect(Collectors.toMap(task -> task,
                task -> {
                    var statusDurationMap = new HashMap<TaskStatus, Duration>();
                    for (int i = 0; i < task.getStatuses().size() - 1; i++) {
                        statusDurationMap.put(task.getStatuses().get(i), Duration.between(task.getStatuses().get(i).startTime(),
                                task.getStatuses().get(i + 1).startTime()));
                    }
                    return statusDurationMap;
                }));
    }

    private static Map<Task, Map<TaskStatus, Duration>> getTaskStatusesDurationMapByCollector(List<Task> tasks) {
        return tasks.stream().collect(TaskStatusStatsCollector.toTaskStatusStatsMap());
    }
}