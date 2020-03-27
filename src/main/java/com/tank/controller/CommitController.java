package com.tank.controller;


import com.tank.client.Client;
import com.tank.common.protocol.CoordinatorReq;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1")
public class CommitController {

  @PostMapping("/coordinate")
  public ResponseEntity<String> commit(@RequestBody @NonNull final CoordinatorReq coordinatorReq) {
    this.client.notificationSelectorNode(coordinatorReq);
    return ResponseEntity.ok("commit");
  }

  @Autowired
  private Client client;
}
