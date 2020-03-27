package com.tank.common.protocol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResult<T> {
  private int status;
  private T data;
  private String error;
}
