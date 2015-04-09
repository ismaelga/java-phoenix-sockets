package org.java_phoenix_sockets;

/**
 * Created by ismael on 04/03/15.
 */

import com.google.gson.Gson;
import org.java_phoenix_sockets.handlers.InternalSocketListener;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Socket {
    private ArrayList<Channel> channels = new ArrayList<Channel>();
    private InternalSocket internalSocket;
    private ArrayList<PayloadSender> sendBuffer = new ArrayList<PayloadSender>();
    private Timer sendBufferTimer = new Timer("sendBufferTimer");
    private Timer reconnectTimer = new Timer("reconnectTimer");
    private URI serverURI;
    private Logger log;

    public Socket(String uri) {
        this(URI.create(uri));
        scheduleFlush();
    }

    public Socket(URI serverURI) {
        this.log = LoggerFactory.getLogger(Socket.class);

        this.serverURI = serverURI;
        log.info("initializing " + this.serverURI);
        initializeSocket();
        log.info("connected " + this.serverURI);
    }

    private void scheduleReconnect() {
        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                initializeSocket();
            }
        }, getReconnectDelay());
    }

    private void scheduleFlush() {
        sendBufferTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                flushBuffer();
            }
        }, getFlushDelay());
    }

    private void flushBuffer() {
        log.info("Flushing buffer");
        if(!isConnected()) {
            log.info("Not connected");
            return;
        }
        if(sendBuffer != null && !sendBuffer.isEmpty()) {
            for(PayloadSender payloadSender : this.sendBuffer) {
                payloadSender.send(this);
            }
            sendBuffer.clear();
            log.info("Buffer flushed");
        } else {
            log.info("Nothing to flush");
        }
    }

    private long getFlushDelay() {
        return 500;
    }

    private long getReconnectDelay() {
        return 500;
    }


    public Channel join(String topic) {
        return join(topic, new Message());
    }
    public Channel join(String topic, Message message) {
        Channel channel = new Channel(topic, message, this);
        this.channels.add(channel);
        rejoin(channel);
        return channel;
    }

    public void close() {
        sendBufferTimer.cancel();
        reconnectTimer.cancel();
        internalSocket.getConnection().close();
    }

    public void leave(String topic) {
        leave(topic, new Message());
    }
    public void leave(String topic, Message message) {
        this.send(topic, "leave", message);
    }

    private void initializeSocket() {
        final Socket _this = this;
        this.internalSocket = new InternalSocket(this.serverURI, new InternalSocketListener() {
            @Override
            public void onOpen() {
                _this.onOpen();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                _this.onClose(code, reason, remote);
            }

            @Override
            public void onMessage(String rawMessage) {
                _this.onMessage(rawMessage);
            }

            @Override
            public void onError(Exception ex) {
                _this.onError(ex);
            }
        });

        this.internalSocket.connect();
    }

    // Socket callback
    private void onMessage(String rawMessage) {
        log.info("onMessage: " + rawMessage);
        Payload payload = payloadFromJson(rawMessage);
        for(Channel channel : this.channels) {
            if(channel.isMember(payload.getTopic())) {
                channel.trigger(payload.getEvent(), payload.getMessage());
            }
        }
    }

    // Socket callback
    private void onError(Exception ex) {
        log.info("onError: " + ex.getMessage());
//        ex.printStackTrace();
    }

    // Socket callback
    private void onOpen() {
        log.info("onOpen");
        reconnectTimer.purge();
        scheduleFlush();
        rejoinAll();
    }

    // Socket callback
    private void onClose(int code, String reason, boolean remote) {
        log.info("onClose: code=" + code + " reason=" + reason + " remote=" + remote);
        reconnectTimer.purge();
        scheduleReconnect();
    }

    private void rejoinAll() {
        for(Channel channel : this.channels) {
            this.rejoin(channel);
        }
    }

    private void rejoin(Channel channel) {
        log.info("rejoin " + channel.getTopic());
        if(isConnected()) {
            this.send(channel.getTopic(), "join", channel.getMessage());
        }
    }

    private boolean isConnected() {
        WebSocket conn;
        return (conn = internalSocket.getConnection()) != null && conn.isOpen();
    }

    private String payloadToJson(Payload payload) {
        return (new Gson()).toJson(payload);
    }

    private Payload payloadFromJson(String rawMessage) {
        return new Gson().fromJson(rawMessage, Payload.class);
    }

    public void send(String topic, String event, Message message) {
        Payload payload = new Payload(topic, event, message);

        PayloadSender payloadSender = new PayloadSender(payload);

        if(isConnected()) {
            payloadSender.send(this);
        } else {
            this.sendBuffer.add(payloadSender);
        }
    }

    public void sendPayload(Payload payload) {
        String message = payloadToJson(payload);
        log.info("SENDING" + message);
        internalSocket.send(message);
    }
}
