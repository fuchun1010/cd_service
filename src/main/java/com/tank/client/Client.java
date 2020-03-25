package com.tank.client;

import com.tank.common.DataPackage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Client implements Runnable {

  public Client() {
    this.socket = this.initSocket();
  }

  private Socket initSocket() {
    String ip = "localhost";
    int port = 10001;
    Socket socket = null;
    try {
      socket = new Socket(ip, port);
      log.info("connect ip:[{}],Port:[{}] success", ip, port);

    } catch (IOException e) {
      e.printStackTrace();
    }
    return socket;
  }


  public void sayHello(String message) {
    int type = 0;
    byte[] data = this.dataPackage.content(type, message.getBytes(), ByteBuffer::array);
    try {
      BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
      out.write(data);
      out.flush();

    } catch (IOException e) {
      log.info("sayAppName error:[{}]", e.getMessage());
    }
    log.info("socket status is:[{}]", socket.isConnected() ? "keep" : "closed");
  }


  @Override
  @SneakyThrows
  public void run() {
    byte[] buffer = new byte[K];
    BufferedInputStream in;
    try {
      for (; ; ) {
        in = new BufferedInputStream(socket.getInputStream());
        int result = in.read(buffer);
        if (result == -1) {
          TimeUnit.MICROSECONDS.sleep(200);
          continue;
        }
        //TODO 策略模式
        ByteBuffer payLoad = ByteBuffer.wrap(buffer);
        int type = payLoad.getInt();
        int len = payLoad.getInt();
        byte[] body = new byte[len];
        payLoad.get(body);
        System.out.println(new String(body));
        payLoad.clear();
        payLoad.flip();
      }
    } catch (Exception e) {
      e.printStackTrace();
      log.info("receive coordinator message exp:[{}]", e.getMessage());
    }
  }


  @Autowired
  private DataPackage dataPackage;

  private Socket socket;

  private final int K = 1024;


}