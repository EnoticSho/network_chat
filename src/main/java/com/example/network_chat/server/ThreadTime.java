package com.example.network_chat.server;

import com.example.network_chat.ChatClient;
import com.example.network_chat.Command;

public class ThreadTime extends Thread{
    private final ChatClient chatClient;
    private final long time;
    private boolean isActive = true;
    private final long AUTH_TIME = 120_000;

    public ThreadTime(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.time = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (isActive){
            if(System.currentTimeMillis() - time >= AUTH_TIME){
                chatClient.sendMessage(Command.END);
                isActive = false;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
