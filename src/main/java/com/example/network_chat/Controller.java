package com.example.network_chat;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.example.messages.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.WindowEvent;


public class Controller {

    @FXML
    private ListView<String> clientList;
    @FXML
    private HBox messageBox;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField loginField;
    @FXML
    private HBox loginBox;
    @FXML
    private TextField textField;
    @FXML
    private TextArea textArea;

    private final ChatClient client;
    private String nickTo;
    private String fileHistory;

    public Controller() {
        client = new ChatClient(this);
        while (true) {
            try {
                client.openConnection();
                break;
            } catch (Exception e) {
                showNotification();
            }
        }
    }

    public void btnSendClick(ActionEvent event) {
        final String message = textField.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        if (nickTo != null) {
            client.sendMessage(PrivateMessage.of(nickTo, client.getNick(), message));
            nickTo = null;
        } else {
            client.sendMessage(SimpleMessage.of(message, client.getNick()));
        }
        textField.clear();
        textField.requestFocus();
    }

    public void addMessage(String message) {
        textArea.appendText(message + "\n");
    }

    public void btnAuthClick(ActionEvent actionEvent) {
        client.sendMessage(AuthMessage.of(loginField.getText(), passwordField.getText()));
        fileHistory = "History" + loginField.getText();
    }

    public void setAuth(boolean success) {
        loginBox.setVisible(!success);
        messageBox.setVisible(success);
    }

    private void showNotification() {
        final Alert alert = new Alert(Alert.AlertType.ERROR,
                "???? ???????? ?????????????????????? ?? ??????????????.\n" +
                        "??????????????????, ?????? ???????????? ??????????????",
                new ButtonType("?????????????????????? ??????", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("??????????", ButtonBar.ButtonData.CANCEL_CLOSE));
        alert.setTitle("???????????? ??????????????????????");
        final Optional<ButtonType> buttonType = alert.showAndWait();
        final Boolean isExit = buttonType.map(btn -> btn.getButtonData().isCancelButton()).orElse(false);
        if (isExit) {
            System.exit(0);
        }
    }

    public void showError(String error) {
        final Alert alert = new Alert(Alert.AlertType.ERROR, error, new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        alert.setTitle("????????????!");
        alert.showAndWait();
    }

    public void selectClient(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            final String message = textField.getText();
            final String nick = clientList.getSelectionModel().getSelectedItem();
            if (nick != null) {
                this.nickTo = nick;
            }
//            textField.setText(Command.PRIVATE_MESSAGE.collectMessage(nick, message));
            textField.requestFocus();
            textField.selectEnd();
        }
    }

    public void updateClientList(Collection<String> clients) {
        clientList.getItems().clear();
        clientList.getItems().addAll(clients);

    }

    public void btnChangeNick(ActionEvent actionEvent) {
        client.sendMessage(ChangeNickMessage.of(client.getNick(), textField.getText()));
        textField.clear();
        textField.requestFocus();
    }

    public void loadHistory() {
        int fixMessage = 100;
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileHistory))) {
            String s;
            while ((s = br.readLine()) != null) {
                list.add(s);
            }
            if (list.size() > fixMessage) {
                for (int i = list.size() - fixMessage; i < list.size(); i++) {
                    textArea.appendText(list.get(i) + "\n");
                }
            }else {
                for (String s1 : list) {
                    textArea.appendText(s1 + "\n");
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private final EventHandler<WindowEvent> closeEventHandler = event -> writeHistory();

    public EventHandler<WindowEvent> getCloseEventHandler(){
        return closeEventHandler;
    }

    public void writeHistory(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileHistory, true))) {
            writer.write(textArea.getText());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}