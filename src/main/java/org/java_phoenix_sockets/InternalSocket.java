package org.java_phoenix_sockets;

/**
 * Created by ismael on 04/03/15.
 */

import org.java_phoenix_sockets.handlers.InternalSocketListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;

public class InternalSocket extends WebSocketClient {
    private ArrayList<Channel> channels = new ArrayList<Channel>();
    private InternalSocketListener listener;

    public InternalSocket(URI serverURI, InternalSocketListener listener) {
        this(serverURI);
        this.listener = listener;
    }

    private InternalSocket(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        listener.onOpen();
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        listener.onClose(code, reason, remote);
    }

    @Override
    public void onMessage(String rawMessage) {
        System.out.println("onMessage " + rawMessage);
        listener.onMessage(rawMessage);
    }


    @Override
    public void onError(Exception ex) {
        listener.onError(ex);
    }
}

