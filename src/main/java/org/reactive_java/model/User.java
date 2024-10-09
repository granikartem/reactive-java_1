package org.reactive_java.model;

import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class User {

    private final String login;

    private final Set<String> groups;
}
