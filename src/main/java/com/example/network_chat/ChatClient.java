package com.example.network_chat;

import java.io.*;
import java.net.Socket;

import com.example.messages.*;
import javafx.application.Platform;

public class ChatClient {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String nick;

    private final Controller controller;

    public ChatClient(Controller controller) {
        this.controller = controller;
    }

    public void openConnection() throws Exception {
        socket = new Socket("localhost", 8109);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        final Thread readThread = new Thread(() -> {
            try {
                waitAuthenticate();
                readMessage();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                closeConnection();
            }
        });
        readThread.setDaemon(true);
        readThread.start();

    }

    private void readMessage() throws Exception {
        while (true) {
            final AbstractMessage message = (AbstractMessage) in.readObject();
            System.out.println("Receive message: " + message);
            if (message.getCommand() == Command.END) {
                controller.setAuth(false);
                break;
            }
            if (message.getCommand() == Command.ERROR) {
                final ErrorMessage errorMessage = (ErrorMessage) message;
                Platform.runLater(() -> controller.showError(errorMessage.getError()));
            } else if (message.getCommand() == Command.CLIENTS) {
                final ClientListMessage clientListMessage = (ClientListMessage) message;
                controller.updateClientList(clientListMessage.getClients());
            } else if (message.getCommand() == Command.MESSAGE) {
                final SimpleMessage simpleMessage = (SimpleMessage) message;
                controller.addMessage(simpleMessage.getNickFrom() + ": " + simpleMessage.getMessage());
            }
        }
    }

    private void waitAuthenticate() throws IOException, ClassNotFoundException {
        while (true) {
            final AbstractMessage message = (AbstractMessage) in.readObject();
            if (message.getCommand() == Command.AUTHOK) {
                controller.addMessage("Успешная авторизация под ником ");
                controller.setAuth(true);
                break;
            }
            if (message.getCommand() == Command.ERROR) {
                final ErrorMessage errorMessage = (ErrorMessage) message;
                Platform.runLater(() -> controller.showError(errorMessage.getError()));
            }
        }
    }

    private void closeConnection() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public void sendMessage(AbstractMessage message) {
        try {
            System.out.println("Send message: " + message);
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }
}
