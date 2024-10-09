package org.reactive_java;

import org.reactive_java.model.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.reactive_java.generator.TaskGenerator.generateTasks;


public class Main {
    public static void main(String[] args) {
        Instant startTime = Instant.now();
        List<Task> tasks = generateTasks(50000);
        Instant endTime = Instant.now();
        System.out.println(tasks.size());
        System.out.println(Duration.between(startTime, endTime).toMillis());
    }
}