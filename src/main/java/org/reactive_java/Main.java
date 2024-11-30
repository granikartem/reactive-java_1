package org.reactive_java;

import org.reactive_java.collector.UserTasksCompletetionCollector;
import org.reactive_java.model.Task;
import org.reactive_java.model.TaskStatus;
import org.reactive_java.model.User;

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
                getCollectorWorkTimeInMillis(Main::getUserTasksCompletionMapByIterator, tasks)));
        System.out.println(MessageFormat.format("Время работы конвейером с помощью Stream API = {0}\n",
                getCollectorWorkTimeInMillis(Main::getUserTasksCompletionMapByStream, tasks)));
        System.out.println(MessageFormat.format("Время работы конвейером с помощью собственного коллектора = {0}\n",
                getCollectorWorkTimeInMillis(Main::getUserTasksCompletionMapByCollector, tasks)));
    }

    private static double getCollectorWorkTimeInMillis(Function<List<Task>, Map<User, Map<Task, Boolean>>> collector,
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

    private static Map<User, Map<Task, Boolean>> getUserTasksCompletionMapByIterator(List<Task> tasks) {
        var userTasksCompletionMap = new HashMap<User, Map<Task, Boolean>>();
        for (Task task : tasks) {
            User user = task.getUser();

            if (!userTasksCompletionMap.containsKey(user)) {
                userTasksCompletionMap.put(user, new HashMap<>());
            }

            var taskStatuses = task.getStatuses();
            boolean taskCompletedOnTime = true;

            for (TaskStatus taskStatus : taskStatuses) {
                taskCompletedOnTime &= Duration.between(taskStatus.startTime(), taskStatus.finishTime())
                        .compareTo(task.getEvaluation().getStatusDurationMap().get(taskStatus.status())) > 0;
            }

            userTasksCompletionMap.get(user).put(task, taskCompletedOnTime);
        }
        return userTasksCompletionMap;
    }

    private static Map<User, Map<Task, Boolean>> getUserTasksCompletionMapByStream(List<Task> tasks){
        return tasks.stream().collect(
                Collectors.toMap(
                        task -> task.getUser(),
                        task -> {
                            boolean taskCompletedOnTime = task.getStatuses()
                                    .stream()
                                    .noneMatch(
                                            taskStatus -> Duration.between(taskStatus.startTime(), taskStatus.finishTime())
                                                    .compareTo(task.getEvaluation().getStatusDurationMap().get(taskStatus.status())) > 0
                                    );

                            var result = new HashMap<Task, Boolean>();
                            result.put(task, taskCompletedOnTime);

                            return new HashMap<>(result);
                        },
                        (map1, map2) -> {
                            map1.putAll(map2);
                            return map1;
                        }
                )
        );
    }

    private static Map<User, Map<Task, Boolean>> getUserTasksCompletionMapByCollector(List<Task> tasks) {
        return tasks.stream().collect(UserTasksCompletetionCollector.toTaskStatusStatsMap());
    }
}