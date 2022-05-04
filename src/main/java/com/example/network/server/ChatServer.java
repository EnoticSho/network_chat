package com.example.network.server;

import com.example.messages.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatServer {

    private final Map<String, ClientHandler> clients;

    public ChatServer() {
        this.clients = new HashMap<>();
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(8109);
             Connection connection = DriverManager.getConnection("jdbc:sqlite:users.db")) { // Соединение с базой
            while (true) {
                System.out.println("Wait client connection...");
                final Socket socket = serverSocket.accept();
                new ClientHandler(socket, this, connection);
                System.out.println("Client connected");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isNickBusy(String nick) {
        return clients.containsKey(nick);
    }

    public void subscribe(ClientHandler client) {
        clients.put(client.getNick(), client);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client.getNick());
        broadcastClientList();
    }

    private void broadcastClientList() {
        final List<String> nicks = clients.values().stream()
                .map(ClientHandler::getNick)
                .collect(Collectors.toList());
        broadcast(ClientListMessage.of(nicks));
    }

    public void broadcast(AbstractMessage message) {
        for (ClientHandler client : clients.values()) {
            client.sendMessage(message);
        }
    }

    public void sendMessageToClient(ClientHandler sender, String to, String message) {
        final ClientHandler receiver = clients.get(to);
        if (receiver != null) {
            receiver.sendMessage(SimpleMessage.of("от " + sender.getNick() + ": " + message, sender.getNick()));
            sender.sendMessage(SimpleMessage.of("участнику " + to + ": " + message, sender.getNick()));
        } else {
            sender.sendMessage(ErrorMessage.of("Участника с ником " + to + " нет в чате!"));
        }
    }

//    public void changeNick(ClientHandler clientHandler, String param, Connection connection) {
//        try (PreparedStatement statement = connection.prepareStatement(String.format("UPDATE users SET nick = '"+ param +"' WHERE nick = ?"))){
//            statement.setString(1, clientHandler.getNick());
//            statement.executeUpdate();
//            clientHandler.sendMessage("Вы успешно изменили ник с " + clientHandler.getNick() + " на " + param);
//            clientHandler.setNick(param);
//            broadcastClientList();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
