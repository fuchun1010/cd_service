package com.tank.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1")
public class CommitController {

  @GetMapping("/commit")
  public ResponseEntity<String> commit() {
    return ResponseEntity.ok("commit");
  }
}
