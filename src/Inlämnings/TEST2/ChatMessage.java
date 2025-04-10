package Inlämnings.TEST2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatMessage {
    @JsonProperty("type")
    private String type;

    @JsonProperty("sender")
    private String sender;

    @JsonProperty("text")
    private String text;

    // Default constructor for Jackson
    public ChatMessage() {}

    public ChatMessage(String type, String sender, String text) {
        this.type = type;
        this.sender = sender;
        this.text = text;
    }

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}