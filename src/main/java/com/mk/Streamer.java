package com.mk;

import org.eclipse.jetty.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Streamer {

  static final Logger LOGGER = LoggerFactory.getLogger(Streamer.class);

  //Синглетон
  static private Streamer streamer = new Streamer();

  private Streamer() {}

  public static Streamer getInstatnce() {
    return streamer;
  }

  private ConcurrentLinkedQueue<WebSocket.Connection> _broadcast = new ConcurrentLinkedQueue<>();

  public void addConnection(WebSocket.Connection connection) {
    _broadcast.add(connection);
  }

  private void sendToAll(String msg) {
    Iterator<WebSocket.Connection> it = _broadcast.iterator();

    while (it.hasNext()) {
      WebSocket.Connection connection = it.next();
      if (connection.isOpen()) {
        try {
          connection.sendMessage(msg);
        } catch (IOException e) {
          LOGGER.error("Error send msg", e);
        }
      } else {
        it.remove();
      }
    }
  }

  public void run() {
    try {
      Robot robot = new Robot();
      Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

      while (true) {
        if (!_broadcast.isEmpty()) {
          BufferedImage image = robot.createScreenCapture(screenRect);
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          ImageIO.write(image, "JPG", out);
          byte[] bytes = out.toByteArray();
          String base64bytes = Base64.getEncoder().encodeToString(bytes);
          String src = "data:image/jpg;base64," + base64bytes;
          sendToAll(src);
        }
      }
    } catch (Exception ex) {
      LOGGER.error("Error there...", ex);
    }
  }

}
