package org.reactive_java.generator;

import org.apache.maven.surefire.shared.lang3.RandomStringUtils;
import org.reactive_java.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.reactive_java.util.Constants.*;

public class TaskGenerator {

    private final static List<User> users = UserGenerator.generateUsers();


    public static List<Task> generateTasks(int amount)
    {
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
                        pickUser(),
                        generateDescription()
                );
    }


    private static Long generateId(){
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
        int taskStatuses = ThreadLocalRandom.current().nextInt(1, STATUS_AMOUNT);

        List<TaskStatus> statuses = new ArrayList<>(taskStatuses);

        for (int i = 0; i < taskStatuses; i++) {
            statuses.add(new TaskStatus(Status.values()[i], startTime));
            startTime = startTime.plusSeconds(ThreadLocalRandom.current().nextLong(MAX_STATUS_DURATION.toSeconds()));
        }
        return statuses;
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
        int startSeconds =  LocalTime.MIN.toSecondOfDay();
        int endSeconds = LocalTime.MAX.toSecondOfDay();
        int randomTime = ThreadLocalRandom
                .current()
                .nextInt(startSeconds, endSeconds);
        return LocalTime.ofSecondOfDay(randomTime);
    }
}
