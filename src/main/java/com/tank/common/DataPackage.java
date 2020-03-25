package com.tank.common;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.function.Function;

@Component
public class DataPackage {

  public byte[] content(int messageType, byte[] data, Function<ByteBuffer, byte[]> fun) {
    int totalLength = 8 + data.length;
    ByteBuffer buffer = ByteBuffer.allocate(totalLength);
    buffer.putInt(messageType);
    buffer.putInt(data.length);
    buffer.put(data);
    byte[] result = fun.apply(buffer);
    buffer.flip();
    buffer.clear();
    return result;
  }
}
