package com.example.network.server;

public class ServerRunner {
    public static void main(String[] args) {
        final ChatServer chatServer = new ChatServer();
        chatServer.run();
    }
}
