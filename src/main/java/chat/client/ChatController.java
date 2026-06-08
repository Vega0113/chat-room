package chat.client;

import chat.model.MessagePacket;
import chat.model.MessagePacket.Type;
import chat.sound.SoundManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

public class ChatController {
    @FXML private Label userLabel, onlineLabel;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox messageBox;
    @FXML private TextField inputField;

    private ChatClient client;
    private String myUser;
    private boolean hasHistory = false;

    public void setClient(ChatClient client, String u) {
        this.client = client; this.myUser = u;
        userLabel.setText("User: " + u);
        inputField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) onSend(); });
        messageBox.heightProperty().addListener((o, old, n) -> scrollPane.setVvalue(1.0));
    }

    @FXML private void onSend() {
        var t = inputField.getText().trim();
        if (t.isEmpty()) return;
        inputField.clear();
        client.send(new MessagePacket(Type.CHAT, myUser, t));
        SoundManager.playSend();
    }

    public void appendChat(MessagePacket p) {
        messageBox.getChildren().add(buildBubble(p.sender(), p.content(), p.timestamp(), p.sender().equals(myUser)));
    }

    public void appendHistory(MessagePacket p) {
        var b = buildBubble(p.sender(), p.content(), p.timestamp(), p.sender().equals(myUser));
        b.setOpacity(0.7);
        messageBox.getChildren().add(b);
    }

    public void appendSystem(MessagePacket p) {
        var l = new Label(p.content());
        l.setStyle("-fx-text-fill: #8899A6; -fx-font-size: 11px; -fx-padding: 8 0 8 0;");
        l.setMaxWidth(Double.MAX_VALUE); l.setAlignment(Pos.CENTER);
        messageBox.getChildren().add(l);
    }

    public void updateOnlineCount(int c) { onlineLabel.setText(c + " Online"); }

    private HBox buildBubble(String s, String c, String t, boolean me) {
        // Content Label
        var b = new Label(c); 
        b.setWrapText(true); 
        b.setMaxWidth(360); 
        b.setPadding(new Insets(8, 14, 8, 14));
        b.setStyle(me ? "-fx-background-color: #2E75B6; -fx-text-fill: white; -fx-background-radius: 18 18 4 18;" : 
                       "-fx-background-color: #E8ECF0; -fx-text-fill: #1C1E21; -fx-background-radius: 18 18 18 4;");
        
        // Timestamp Label
        var timeLabel = new Label(t);
        timeLabel.setStyle("-fx-text-fill: #8E9194; -fx-font-size: 9px;");
        
        var col = new VBox(2);
        if (!me) {
            var senderLabel = new Label(s);
            senderLabel.setStyle("-fx-text-fill: #5B626A; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 0 2 4;");
            col.getChildren().add(senderLabel);
        }
        
        col.getChildren().addAll(b, timeLabel);
        col.setAlignment(me ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        
        var row = new HBox(col); 
        row.setPadding(new Insets(4, 12, 4, 12)); 
        row.setAlignment(me ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return row;
    }
}
