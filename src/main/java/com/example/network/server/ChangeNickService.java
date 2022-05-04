package com.example.network.server;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChangeNickService implements Closeable {
    private static final String DB_CONNECTION_URL = "jdbc:sqlite:users.db";
    private final Connection connection;

    private final ClientHandler clientHandler;


    ChangeNickService(ClientHandler clientHandler){
        this.clientHandler = clientHandler;
        try {
            connection = DriverManager.getConnection(DB_CONNECTION_URL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeNick(String param) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE users SET nick = ? WHERE nick = ?")){
            statement.setString(1, param);
            statement.setString(2, clientHandler.getNick());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
