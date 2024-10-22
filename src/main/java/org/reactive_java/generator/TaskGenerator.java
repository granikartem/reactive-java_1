package org.reactive_java.generator;

import org.apache.maven.surefire.shared.lang3.RandomStringUtils;
import org.reactive_java.model.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.reactive_java.util.Constants.*;

public class TaskGenerator {

    private final static List<User> users = UserGenerator.generateUsers();


    public static List<Task> generateTasks(int amount) {
        List<Task> tasks = new ArrayList<>(amount);

        for (int i = 0; i < amount; i++) {
            tasks.add(generateTask());
        }

        return tasks;
    }

    private static Task generateTask() {
        LocalDateTime startTime = generateCreateTime();

        return new Task(
                generateId(),
                generateTaskNumber(),
                startTime,
                generateTaskPriority(),
                generateStatuses(startTime),
                generateEvaluation(),
                pickUser(),
                generateDescription()
        );
    }


    private static Long generateId() {
        return ThreadLocalRandom.current().nextLong();
    }

    private static String generateTaskNumber() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    private static LocalDateTime generateCreateTime() {
        return LocalDateTime.of(
                generateRandomDate(),
                generateRandomTime()
        );
    }

    private static TaskPriority generateTaskPriority() {
        return TaskPriority.values()[ThreadLocalRandom.current().nextInt(TaskPriority.values().length)];
    }

    private static List<TaskStatus> generateStatuses(LocalDateTime startTime) {
        List<TaskStatus> statuses = new ArrayList<>(STATUS_AMOUNT);

        for (int i = 0; i < STATUS_AMOUNT; i++) {
            LocalDateTime finishTime = startTime.plusSeconds(ThreadLocalRandom.current().nextLong(MAX_STATUS_DURATION.toSeconds()));
            statuses.add(new TaskStatus(Status.values()[i], startTime, finishTime));
            startTime = finishTime;
        }
        return statuses;
    }

    private static Evaluation generateEvaluation() {
        Duration totalDuration = Duration.ZERO;
        Map<Status, Duration> statusDurationMap = new HashMap<>();

        for (int i = 0; i < STATUS_AMOUNT; i++) {
            Duration duration = generateDuration();
            totalDuration = totalDuration.plus(duration);
            statusDurationMap.put(Status.values()[i], duration);
        }

        return new Evaluation(totalDuration, statusDurationMap, pickUser());
    }


    private static User pickUser() {
        return users.get(ThreadLocalRandom.current().nextInt(USER_AMOUNT));
    }

    private static String generateDescription() {
        return RandomStringUtils.randomAlphanumeric(100);
    }

    private static LocalDate generateRandomDate() {
        long startEpochDay = START_DATE.toEpochDay();
        long endEpochDay = END_DATE.toEpochDay();
        long randomDay = ThreadLocalRandom
                .current()
                .nextLong(startEpochDay, endEpochDay);

        return LocalDate.ofEpochDay(randomDay);
    }

    private static LocalTime generateRandomTime() {
        int startSeconds = LocalTime.MIN.toSecondOfDay();
        int endSeconds = LocalTime.MAX.toSecondOfDay();
        int randomTime = ThreadLocalRandom
                .current()
                .nextInt(startSeconds, endSeconds);
        return LocalTime.ofSecondOfDay(randomTime);
    }

    private static Duration generateDuration() {
        return  Duration.of(ThreadLocalRandom.current().nextLong(MAX_STATUS_DURATION.toSeconds()), ChronoUnit.SECONDS);
    }
}
