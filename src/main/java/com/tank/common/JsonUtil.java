package com.tank.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class JsonUtil {

  public JsonUtil() {
    super();
    this.objectMapper = new ObjectMapper();
  }

  public <T> Optional<String> toJsonStr(@NonNull final T data) {
    try {
      return Optional.of(this.objectMapper.writeValueAsString(data));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public <T> Optional<T> toObject(@NonNull final String jsonStr, Class<T> clazz) {
    try {
      return Optional.of(this.objectMapper.readValue(jsonStr.getBytes(), clazz));
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }


  private ObjectMapper objectMapper = null;

}
