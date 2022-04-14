package com.example.network_chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient {
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;

    private ClientController controller;

    public ChatClient(ClientController controller){
        this.controller = controller;
    }

    public void openConnection() throws IOException{
        socket = new Socket("localhost", 8189);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                waitAuth();
                readMessage();
            }finally {
                closeConnection();
            }
        }).start();
    }

    private void closeConnection() {

    }

    private void readMessage() {
        while (true){
            try {
                String s = in.readUTF();
                if ("/end".equals(s)){
                    controller.setAuth(false);
                    break;
                }
                controller.addMessage(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void waitAuth() {
        while (true){
            try {
                final String msg = in.readUTF();
                if (msg.startsWith("/authok")){
                    String[] s = msg.split(" ");
                    String nick = s[1];
                    controller.addMessage("success auth " + nick);
                    controller.setAuth(true);
                    break;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String s) {
        try {
            out.writeUTF(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
