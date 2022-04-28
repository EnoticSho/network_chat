module com.example.network_chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.network_chat to javafx.fxml;
    exports com.example.network_chat;
    exports com.example.network.server;
    opens com.example.network.server to javafx.fxml;
    exports com.example.messages;
    opens com.example.messages to javafx.fxml;
}