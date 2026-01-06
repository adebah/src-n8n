package com.example.em.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class EmController {

  @PostMapping("/estimate")
  public ResponseEntity<Map<String, Object>> estimate(@RequestBody Map<String, Object> body) {
    Map<String, Object> resp = new LinkedHashMap<>();
    resp.put("pdlpce", Objects.toString(body.getOrDefault("pdlpce", "PDL-MOCK")));
    resp.put("annualKWh", 4200);
    resp.put("method", "mock-average");
    return ResponseEntity.ok(resp);
  }
}
