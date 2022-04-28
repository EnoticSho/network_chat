package com.example.messages;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Command {

    AUTH,
    AUTHOK,
    PRIVATE_MESSAGE,
    END,
    ERROR,
    CLIENTS,
    CHANGENICK,
    MESSAGE;
}