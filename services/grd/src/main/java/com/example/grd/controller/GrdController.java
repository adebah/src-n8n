package com.example.grd.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
public class GrdController {

  private static int simpleHash(String s) {
    if (s == null) return 0;
    int h = 0;
    for (byte b : s.getBytes(StandardCharsets.UTF_8)) {
      h = (h * 31 + b) & 0x7fffffff;
    }
    return h;
  }

  @PostMapping("/meter/search")
  public ResponseEntity<Map<String, Object>> meterSearch(@RequestBody Map<String, Object> payload) {
    Object adrObj = payload.get("adresse");
    String seed = String.valueOf(adrObj);
    String pdlpce = "PDL-" + simpleHash(seed);
    return ResponseEntity.ok(Map.of("pdlpce", pdlpce));
  }

  @PostMapping("/consent")
  public ResponseEntity<Map<String, Object>> consent(@RequestBody Map<String, Object> payload) {
    Map<String, Object> resp = new LinkedHashMap<>();
    resp.put("ok", true);
    resp.put("received", payload);
    return ResponseEntity.ok(resp);
  }

  @PostMapping("/meter/read")
  public ResponseEntity<Map<String, Object>> meterRead(@RequestBody Map<String, Object> payload) {
    String pdlpce = Objects.toString(payload.getOrDefault("pdlpce", "PDL-MOCK"));
    Map<String, Object> resp = new LinkedHashMap<>();
    resp.put("pdlpce", pdlpce);
    resp.put("puissance", 9);
    resp.put("heuresCreuses", true);
    resp.put("lastIndex", 12345);
    return ResponseEntity.ok(resp);
  }

  @PostMapping("/meter/upsert")
  public ResponseEntity<Map<String, Object>> meterUpsert(@RequestBody Map<String, Object> payload) {
    String pdlpce = Objects.toString(payload.getOrDefault("pdlpce", "PDL-" + System.currentTimeMillis()));
    return ResponseEntity.ok(Map.of("pdlpce", pdlpce));
  }

  @PostMapping("/installation/upsert")
  public ResponseEntity<Map<String, Object>> installationUpsert(@RequestBody Map<String, Object> payload) {
    return ResponseEntity.ok(Map.of("installationId", "INST-" + System.currentTimeMillis()));
  }
}
