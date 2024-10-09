package org.reactive_java.generator;

import org.apache.maven.surefire.shared.lang3.RandomStringUtils;
import org.reactive_java.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.reactive_java.util.Constants.*;

public class UserGenerator {
    public static List<User> generateUsers() {
        List<User> users = new ArrayList<>(USER_AMOUNT);

        for (int i = 0; i < USER_AMOUNT; i++) {
            users.add(new User(RandomStringUtils.randomAlphanumeric(10), getRandomGroups()));
        }

        return users;
    }

    private static Set<String> getRandomGroups() {
        int maxGroupAmount = ThreadLocalRandom.current().nextInt(MAX_GROUPS_PER_USER);
        Set<String> groups = new HashSet<>(maxGroupAmount);
        for (int i = 0; i < maxGroupAmount; i++) {
            groups.add(USER_GROUPS.get(ThreadLocalRandom.current().nextInt(USER_GROUPS.size())));
        }
        return groups;
    }
}
