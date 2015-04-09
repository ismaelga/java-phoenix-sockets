package org.java_phoenix_sockets;

/**
 * Created by ismael on 05/03/15.
 */

public class PayloadSender {
    private Payload payload;

    PayloadSender(Payload payload) {
        this.payload = payload;
    }

    public void send(Socket socket) {
        socket.sendPayload(payload);
    }
}
