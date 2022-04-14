package com.example.network_chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private final AuthService authService;
    private final List<ClientHandler> clients;

    public ChatServer() {
        this.authService = new InMemoryAuthService();
        this.clients = new ArrayList<>();
        authService.start();
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                System.out.println("wait for client");
                final Socket socket = serverSocket.accept();
                System.out.println("client is connected");
                new ClientHandler(socket, this, authService);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сервера", e);
        }
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler client : clients) {
            if (client.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void broadcast(String s) {
        for (ClientHandler client : clients) {
            client.sendMessage(s);
        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}
