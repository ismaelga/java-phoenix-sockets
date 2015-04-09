package org.java_phoenix_sockets;

import org.java_phoenix_sockets.handlers.OnEventListener;

/**
 * Created by ismael on 06/03/15.
 */
public class Example {

    @SuppressWarnings("InfiniteLoopStatement")
    static public void main(String[] args) throws InterruptedException {
        Socket socket = new Socket("http://localhost:4000/ws");

        Channel channel = socket.join("rooms:lobby");
        channel.on("new:msg", new OnEventListener() {
            @Override
            public void onEvent(Message message) {
                System.out.println(message.get("user") + ": " + message.get("body"));
            }
        });

        Message message = new Message();
        message.put("user", "ismaelga");
        message.put("body", "message content");
        channel.send("new:msg", message);

        // OR using socket directly
        // socket.send("rooms:lobby", "new:msg", message);

        int i = 0;
        while(true) {
            Thread.sleep(2000);
            message.put("id", ++i);
            System.out.println("Sending... " + i);
            channel.send("new:msg", message);
        }
    }
}
