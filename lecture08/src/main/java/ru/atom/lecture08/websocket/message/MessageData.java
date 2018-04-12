package ru.atom.lecture08.websocket.message;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class MessageData {
    private final String sender;
    private final String msg;

    public MessageData(String sender, String msg) {
        this.sender = sender;
        this.msg = msg;
    }

    @JsonCreator
    public MessageData(@JsonProperty("sender") JsonNode sender, @JsonProperty("msg") JsonNode msg) {
        this.sender = sender.toString();
        this.msg = msg.toString();
    }

    public String getSender() {
        return sender;
    }

    public String getMsg() {
        return msg;
    }
}