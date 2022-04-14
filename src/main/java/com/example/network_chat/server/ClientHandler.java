package com.example.network_chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private ChatServer chatServer;
    private String nick;
    private final DataInputStream in;
    private final DataOutputStream out;
    private AuthService authService;

    public ClientHandler(Socket socket, ChatServer chatServer, AuthService authService) {
        try {
            this.socket = socket;
            this.chatServer = chatServer;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.authService = authService;

            new Thread(() -> {
                try {
                    authenticate();
                    readMessage();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания подключения", e);
        }
    }

    private void readMessage() {
        while (true) {
            try {
                final String msg = in.readUTF();
                System.out.println("message get: " + getNick() + ": " + msg);
                if ("/end".equals(msg)) {
                    break;
                }
                // отправка сообщения из одного слова и без проверок есть ли этот пользователь вообще
                if (msg.startsWith("/w ")) {
                    String to = msg.split(" ")[1];
                    String newMsg = msg.split(" ")[2];
                    chatServer.broadcast(this, to, newMsg);
                }else chatServer.broadcast("[" + getNick() + "] " + msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void authenticate() {
        while (true) {
            try {
                final String msg = in.readUTF();
                if (msg.startsWith("/auth")) {
                    final String[] s = msg.split("\\s+");
                    final String login = s[1];
                    final String password = s[2];
                    final String nick = authService.getNickByLoginAndPassword(login, password);
                    if (nick != null) {
                        if (chatServer.isNickBusy(nick)) {
                            sendMessage("busy");
                            continue;
                        }
                        sendMessage("/authok " + nick);
                        this.nick = nick;
                        chatServer.broadcast("Пользователь " + nick + " вошел в чат");
                        chatServer.subscribe(this);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeConnection() {
        sendMessage("/end");
        try {
            if (in != null) in.close();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка подключения");
        }
        try {
            if (socket != null) {
                chatServer.unsubscribe(this);
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка подключения");
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка подключения");
        }
    }

    public void sendMessage(String s) {
        try {
            System.out.println("sending message: " + s);
            out.writeUTF(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNick() {
        return nick;
    }
}
