package com.example.network.server;

import java.sql.*;

public class DbAuthService implements AuthService {

    private static final String DB_CONNECTION_URL = "jdbc:sqlite:users.db";

    private Connection connection;

    public DbAuthService() {
        run();
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        try (final PreparedStatement statement = connection.prepareStatement("select nick from users where login = ? and password = ?")) {
            statement.setString(1, login);
            statement.setString(2, password);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("nick");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        try {
            connection = DriverManager.getConnection(DB_CONNECTION_URL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
