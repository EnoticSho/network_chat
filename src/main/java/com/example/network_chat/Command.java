package com.example.network_chat;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Command {

    AUTH("/auth") {
        @Override
        public String[] parse(String commandText) {
            final String[] split = commandText.split(COMMAND_DELIMITER);
            return new String[]{split[1], split[2]};
        }
    },
    AUTHOK("/authok") {
        @Override
        public String[] parse(String commandText) {
            return new String[]{commandText.split(COMMAND_DELIMITER)[1]};
        }
    },
    PRIVATE_MESSAGE("/w") {
        @Override
        public String[] parse(String commandText) {
            final String[] split = commandText.split(COMMAND_DELIMITER, 3);
            return new String[]{split[1], split[2]};
        }
    },
    END("/end") {
        @Override
        public String[] parse(String commandText) {
            return new String[0];
        }
    }, // /end
    ERROR("/error") {
        @Override
        public String[] parse(String commandText) {
            final String errorMsg = commandText.split(COMMAND_DELIMITER, 2)[1];
            return new String[]{errorMsg};
        }
    },
    CLIENTS("/clients") {
        @Override
        public String[] parse(String commandText) {
            final String[] split = commandText.split(COMMAND_DELIMITER);
            final String[] nicks = new String[split.length - 1];
            for (int i = 1; i < split.length; i++) {
                nicks[i - 1] = split[i];
            }
            return nicks;
        }
    },
    CHANGENICK("/changeNick") {
        @Override
        public String[] parse(String commandText) {
            final String[] split = commandText.split(COMMAND_DELIMITER, 2);
            return new String[]{split[1]};
        }
    };

    private static final Map<String, Command> map = Stream.of(Command.values())
            .collect(Collectors.toMap(Command::getCommand, Function.identity()));

    private String command;
    private String[] params = new String[0];

    static final String COMMAND_DELIMITER = "\\s+";

    Command(String command) {
        this.command = command;
    }

    public static boolean isCommand(String message) {
        return message.startsWith("/");
    }

    public String[] getParams() {
        return params;
    }

    public String getCommand() {
        return command;
    }

    public static Command getCommand(String message) {
        message = message.trim();
        if (!isCommand(message)) {
            throw new RuntimeException("'" + message + "' is not a command");
        }
        final int index = message.indexOf(" ");

        String cmd;
        if (index > 0) {
            cmd = message.substring(0, index);
        } else {
            cmd = message;
        }
        final Command command = map.get(cmd);
        if (command == null) {
            throw new RuntimeException("'" + cmd + "' unknown command");
        }
        return command;
    }

    public abstract String[] parse(String commandText);

    public String collectMessage(String... params) {
        final String command = this.getCommand();
        return command +
                (params == null
                        ? ""
                        : " " + String.join(" ", params));
    }
}