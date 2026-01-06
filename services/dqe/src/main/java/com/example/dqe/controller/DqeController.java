package com.example.dqe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping
public class DqeController {

  @PostMapping("/address/validate")
  public ResponseEntity<Map<String, Object>> validateAddress(@RequestBody Map<String, Object> address) {
    Map<String, Object> validated = new LinkedHashMap<>();
    validated.put("ligne1", Objects.toString(address.getOrDefault("ligne1", address.getOrDefault("line1", "UNKNOWN")), "UNKNOWN"));
    validated.put("codePostal", Objects.toString(address.getOrDefault("codePostal", "00000"), "00000"));
    validated.put("ville", Objects.toString(address.getOrDefault("ville", "UNKNOWN"), "UNKNOWN"));
    validated.put("isValid", true);
    return ResponseEntity.ok(Map.of("validated", validated));
  }

  @PostMapping("/email/validate")
  public ResponseEntity<Map<String, Object>> validateEmail(@RequestBody Map<String, Object> body) {
    return ResponseEntity.ok(Map.of("isValid", true));
  }

  @PostMapping("/phone/validate")
  public ResponseEntity<Map<String, Object>> validatePhone(@RequestBody Map<String, Object> body) {
    return ResponseEntity.ok(Map.of("isValid", true));
  }
}
