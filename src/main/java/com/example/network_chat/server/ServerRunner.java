package com.example.network_chat.server;

public class ServerRunner {
    public static void main(String[] args) {
        final ChatServer chatServer = new ChatServer();
        chatServer.run();
    }
}
