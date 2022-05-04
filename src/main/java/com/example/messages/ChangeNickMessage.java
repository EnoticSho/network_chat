package com.example.messages;

public class ChangeNickMessage extends AbstractMessage{

    private final String oldNick;
    private final String newNick;
    public ChangeNickMessage(String oldNick, String newNick) {
        super(Command.CHANGENICK);
        this.oldNick = oldNick;
        this.newNick = newNick;
    }

    public static ChangeNickMessage of(String oldNick, String newNick) {
        return new ChangeNickMessage(oldNick, newNick);
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder("ChangeNickMessage{");
        sb.append("timestamp=").append(getTimestamp());
        sb.append(", nick='").append(newNick).append("'");
        sb.append('}');
        return sb.toString();
    }


    public String getOldNick() {
        return oldNick;
    }

    public String getNewNick() {
        return newNick;
    }
}
