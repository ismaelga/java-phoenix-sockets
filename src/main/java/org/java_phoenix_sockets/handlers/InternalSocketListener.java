package org.java_phoenix_sockets.handlers;

/**
 * Created by ismael on 06/03/15.
 */

public interface InternalSocketListener {

    void onOpen();

    void onClose(int code, String reason, boolean remote);

    void onMessage(String rawMessage);

    void onError(Exception ex);

}
