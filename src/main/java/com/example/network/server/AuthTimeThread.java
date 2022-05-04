package com.example.network.server;

public class AuthTimeThread extends Thread{
    private final ClientHandler clientHandler;
    private final long AUTH_TIME = 120_000;

    public AuthTimeThread(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void run() {
            try {
                Thread.sleep(AUTH_TIME);
                clientHandler.closeConnection();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
    }
}
