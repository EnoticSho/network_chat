package com.example.network_chat.server;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthService implements AuthService {
    private List<UserData> list;

    private class UserData{
        private String login;
        private String password;
        private String nick;

        private UserData(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }
    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        for (UserData userData : list) {
            if (userData.login.equals(login) && userData.password.equals(password)){
                return userData.nick;
            }
        }
        return null;
    }

    @Override
    public void start() {
        list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new UserData("login" + i, "pass" + i, "nick" + i));
        }
    }

    @Override
    public void close() {
        System.out.println("Сервис аутентификации установлен");
    }
}
