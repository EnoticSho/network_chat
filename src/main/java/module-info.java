module com.example.network_chat {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.network_chat to javafx.fxml;
    exports com.example.network_chat;
}