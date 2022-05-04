package com.example.messages;

public class ChangeNickMessage extends AbstractMessage{

    private final String nick;
    public ChangeNickMessage(String nick) {
        super(Command.CHANGENICK);
        this.nick = nick;
    }

    public static ChangeNickMessage of(String nick) {
        return new ChangeNickMessage(nick);
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder("ChangeNickMessage{");
        sb.append("timestamp=").append(getTimestamp());
        sb.append(", nick='").append(nick).append("'");
        sb.append('}');
        return sb.toString();
    }

    public String getNick() {
        return nick;
    }
}
