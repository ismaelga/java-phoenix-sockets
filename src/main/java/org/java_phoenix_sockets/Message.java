package org.java_phoenix_sockets;

import java.util.HashMap;

/**
 * Created by ismael on 05/03/15.
 */

public class Message extends HashMap {
    public Message clone() {
        return (Message)super.clone();
    }
}
