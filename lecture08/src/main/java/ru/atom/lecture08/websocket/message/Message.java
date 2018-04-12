package ru.atom.lecture08.websocket.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import ru.atom.lecture08.websocket.util.*;

public class Message {
    private final Topic topic;
    private final MessageData data;

    /*
    public Message(Topic topic, MessageData data) {
        this.topic = topic;
        this.data = data;
    }
    */

    @JsonCreator
    public Message(@JsonProperty("topic") Topic topic, @JsonProperty("data") MessageData data) {
        this.topic = topic;
        this.data = data;
    }

    Topic getTopic() {
        return topic;
    }

    MessageData getData() {
        return data;
    }

    @Override
    public String toString() {
        return JsonHelper.toJson(this);
    }
}
