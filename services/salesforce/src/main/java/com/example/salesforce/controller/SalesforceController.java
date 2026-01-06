package com.example.salesforce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class SalesforceController {

  @PostMapping("/saves/1")
  public ResponseEntity<Map<String, Object>> save1(@RequestBody Map<String, Object> body) {
    if (Boolean.TRUE.equals(body.get("forceError"))) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "forced_error"));
    }
    return ResponseEntity.ok(Map.of("status", "ok", "save", 1));
  }

  @PostMapping("/cases")
  public ResponseEntity<Map<String, Object>> cases(@RequestBody Map<String, Object> body) {
    String context = Objects.toString(body.getOrDefault("context", "save1"));
    return ResponseEntity.ok(Map.of("caseId", "CASE-" + System.currentTimeMillis(), "context", context));
  }
}
