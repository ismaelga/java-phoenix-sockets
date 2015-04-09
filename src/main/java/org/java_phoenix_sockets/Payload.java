package org.java_phoenix_sockets;

import java.io.Serializable;

/**
 * Created by ismael on 05/03/15.
 */

public class Payload implements Serializable {
    private Message payload;
    private String event;
    private String topic;

    public Payload(String topic, String event, Message message) {
        this.topic = topic;
        this.event = event;
        this.payload = message.clone();
    }

    public Message getMessage() {
        return payload;
    }

    public String getTopic() {
        return topic;
    }

    public String getEvent() {
        return event;
    }
}
