package com.tank.client;

import com.tank.common.DataPackage;
import com.tank.common.JsonUtil;
import com.tank.common.message.NodeSelectorMessage;
import com.tank.common.protocol.CoordinatorReq;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author tank198435163.com
 */
@Slf4j
@Component
public class Client implements Runnable {

  public Client() {
    this.socket = this.initSocket();
  }

  public void notificationSelectorNode(final CoordinatorReq coordinatorReq) {
    int type = 1;
    this.sendMessage(type, jsonUtil.toJsonStr(coordinatorReq).orElse("-"), this::sendMessage);
  }

  public void sayHello(String message) {
    int type = 0;
    this.sendMessage(type, message, this::sendMessage);
  }

  public void sendMessage(int type, String message, Consumer<byte[]> consumer) {
    byte[] data = this.dataPackage.content(type, message.getBytes(), ByteBuffer::array);
    consumer.accept(data);
  }

  private void sendMessage(byte[] data) {
    try {
      BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
      out.write(data);
      out.flush();
    } catch (IOException e) {
      log.info("send message error:[{}]", e.getMessage());
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

        //TODO 策略模式 粘包
        ByteBuffer payLoad = ByteBuffer.wrap(buffer);
        int type = payLoad.getInt();

        int len = payLoad.getInt();

        byte[] body = new byte[len];
        payLoad.get(body);
        payLoad.clear();
        payLoad.flip();

        String resultStr = new String(body);

        if (type == NodeSelectorMessage.Hello.ordinal()) {
          System.out.println(resultStr);
        } else if (type == NodeSelectorMessage.Coordinator.ordinal()) {
          JSONParser jsonObject = new JSONParser();
          JSONObject json = ((JSONObject) jsonObject.parse(resultStr));
          for (Object jsonKey : json.entrySet()) {
            List<JSONObject> rollbacks = ((List) json.get("rollbacks"));
            for (JSONObject object : rollbacks) {
              String url = ((String) object.get("url"));
              JSONObject payload = ((JSONObject) object.get("payLoad"));
              //TODO 发消息
              restTemplate.postForLocation(url, payload);
            }
          }
          System.out.println(json);
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
      log.info("receive coordinator message exp:[{}]", e.getMessage());
    }
  }

  private Socket initSocket() {
    //TODO remove hard coding for ip and port
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

  @Autowired
  private DataPackage dataPackage;

  private Socket socket;

  private final int K = 1024;

  @Autowired
  private JsonUtil jsonUtil;

  @Autowired
  private RestTemplate restTemplate;


}
