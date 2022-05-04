package com.example.network.server;

import com.example.messages.*;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private final AuthTimeThread authTimeThread;
    private final Socket socket;
    private final ChatServer server;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final AuthService authService;

    private String nick;

    public ClientHandler(Socket socket, ChatServer server, AuthService authService) {
        try {
            this.nick = "";
            this.socket = socket;
            this.server = server;
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.authService = authService;

            authTimeThread = new AuthTimeThread(this);
            authTimeThread.start();
            new Thread(() -> {
                try {
                    authenticate();
                    readMessages();
                } finally {
                    closeConnection();
                }
            }).start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void closeConnection() {
        sendMessage(EndMessage.of());
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                server.unsubscribe(this);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authenticate() {
        while (true) {
            try {
                final AbstractMessage message = (AbstractMessage) in.readObject();
                if (message.getCommand() == Command.AUTH) {
                    final AuthMessage authMessage = (AuthMessage) message;
                    final String login = authMessage.getLogin();
                    final String password = authMessage.getPassword();
                    final String nick = authService.getNickByLoginAndPassword(login, password);
                    if (nick != null) {
                        if (server.isNickBusy(nick)) {
                            sendMessage(ErrorMessage.of("Пользователь уже авторизован"));
                            continue;
                        }
                        authTimeThread.interrupt();
                        sendMessage(AuthOkMessage.of(nick));
                        this.nick = nick;
                        server.broadcast(SimpleMessage.of(nick, "Пользователь " + nick + " зашел в чат"));
                        server.subscribe(this);
                        break;
                    } else {
                        sendMessage(ErrorMessage.of("Неверные логин и пароль"));
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessage(AbstractMessage message) {
        try {
            System.out.println("SERVER: Send message to " + message);
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() {
        try {
            while (true) {
                final AbstractMessage message = (AbstractMessage) in.readObject();
                System.out.println("Receive message: " + message);
                if (message.getCommand() == Command.END) {
                    break;
                }
                if (message.getCommand() == Command.MESSAGE) {
                    final SimpleMessage simpleMessage = (SimpleMessage) message;
                    server.broadcast(simpleMessage);
                }
                if (message.getCommand() == Command.PRIVATE_MESSAGE) {
                    final PrivateMessage privateMessage = (PrivateMessage) message;
                    server.sendMessageToClient(this, privateMessage.getNickTo(), privateMessage.getMessage());
                }
                if (message.getCommand() == Command.CHANGENICK) {
                    try (ChangeNickService changeNickService = new ChangeNickService(this)){
                        ChangeNickMessage changeNickMessage = (ChangeNickMessage) message;
                        changeNickService.changeNick(changeNickMessage.getNick());
                        this.nick = changeNickMessage.getNick();

                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public String getNick() {
        return nick;
    }
}
