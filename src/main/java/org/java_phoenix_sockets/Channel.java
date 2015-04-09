package org.java_phoenix_sockets;

import org.java_phoenix_sockets.handlers.OnEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ismael on 05/03/15.
 */

public class Channel {
    private Socket socket;
    private String topic;
    private Message message;
    private HashMap<String, HashSet<OnEventListener>> bindings;

    public Channel(String topic, Message message, Socket socket) {
        this.topic = topic;
        this.message = message;
        this.socket = socket;
        reset();
    }

    public String getTopic() {
        return topic;
    }

    public void on(String event, OnEventListener listener) {
        if(bindings.containsKey(event)) {
            HashSet<OnEventListener> eventListeners = bindings.get(event);
            eventListeners.add(listener);
        } else {
            HashSet<OnEventListener> eventListeners = new HashSet<OnEventListener>();
            eventListeners.add(listener);
            bindings.put(event, eventListeners);
        }
    }

    public void send(String event, Message message) {
        socket.send(this.getTopic(), event, message);
    }

    public boolean isMember(String topic) {
        return this.topic.equals(topic);
    }

    public void trigger(String event, Message message) {
        Set<OnEventListener> eventBindings = bindings.get(event);
        if(eventBindings != null) {
            for(OnEventListener listener : eventBindings) {
                listener.onEvent(message);
            }
        }
    }

    public void leave() {
        socket.leave(topic);
    }
    public void leave(Message message) {
        socket.leave(topic, message);
    }

    public void reset() {
        if(this.bindings == null) {
            this.bindings = new HashMap<String, HashSet<OnEventListener>>();
        } else {

        }
    }

    public Message getMessage() {
        if(message != null) {
            return message;
        } else {
            return new Message();
        }
    }

    public void changeSocket(Socket socket) {
        this.socket = socket;
    }
}
