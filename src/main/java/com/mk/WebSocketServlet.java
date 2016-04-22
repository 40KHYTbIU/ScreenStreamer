package com.mk;

import org.eclipse.jetty.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class WebSocketServlet extends org.eclipse.jetty.websocket.WebSocketServlet {
    static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServlet.class);

    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest httpRequest, String protocol) {
        final String id = httpRequest.getParameter("id");
        return new WebSocket.OnTextMessage() {

            @Override
            public void onMessage(String data) {
                LOGGER.debug("From {} got message: {}", id, data);
            }

            @Override
            public void onOpen(WebSocket.Connection connection) {
                Streamer.getInstatnce().addConnection(connection);
                LOGGER.debug("New connection with id: {}", id);
            }

            @Override
            public void onClose(int closeCode, String message) {
                LOGGER.debug("Close connection for {}", id);
            }
        };
    }
}