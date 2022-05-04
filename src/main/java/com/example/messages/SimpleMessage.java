package com.example.messages;

public class SimpleMessage extends AbstractMessage {
    private final String message;
    private final String nickFrom;

    public SimpleMessage(String message, String nickFrom) {
        super(Command.MESSAGE);
        this.message = message;
        this.nickFrom = nickFrom;
    }

    public SimpleMessage(String message) {
        super(Command.MESSAGE);
        this.message = message;
        nickFrom = null;
    }

    public String getMessage() {
        return message;
    }

    public String getNickFrom() {
        return nickFrom;
    }

    public static SimpleMessage of(String message, String nickFrom) {
        return new SimpleMessage(message, nickFrom);
    }
    public static SimpleMessage of(String message) {
        return new SimpleMessage(message);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BroadcastMessage{");
        sb.append("timestamp=").append(getTimestamp());
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
