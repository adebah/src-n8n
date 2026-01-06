package com.example.agilab.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class AgilabController {

  @PostMapping("/getgrille")
  public ResponseEntity<Map<String, Object>> getGrille() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("retrievedAt", new Date().toInstant().toString());
    List<Map<String, Object>> grids = new ArrayList<>();
    grids.add(Map.of("energieType", "electricite", "offre", "standard", "prixKwh", 0.215, "abonnement", 12.5));
    grids.add(Map.of("energieType", "gaz", "offre", "standard", "prixKwh", 0.115, "abonnement", 9.8));
    body.put("grids", grids);
    return ResponseEntity.ok(body);
  }

  @PostMapping("/calculette")
  public ResponseEntity<Map<String, Object>> calculette(@RequestBody Map<String, Object> payload) {
    String energyType = Optional.ofNullable((String) payload.get("energyType")).orElse("electricite");
    String pdlpce = Optional.ofNullable((String) payload.get("pdlpce")).orElse("PDL-MOCK");

    double prixKwh = "gaz".equalsIgnoreCase(energyType) ? 0.115 : 0.215;
    double abonnement = "gaz".equalsIgnoreCase(energyType) ? 9.8 : 12.5;

    @SuppressWarnings("unchecked")
    Map<String, Object> estimation = (Map<String, Object>) payload.getOrDefault("estimation", Collections.emptyMap());
    Number kwh = (Number) estimation.getOrDefault("annualKWh", 4200);

    double total = Math.round((kwh.doubleValue() * prixKwh + abonnement * 12) * 100.0) / 100.0;

    Map<String, Object> resp = new LinkedHashMap<>();
    resp.put("quoteId", "Q-" + System.currentTimeMillis());
    resp.put("pdlpce", pdlpce);
    resp.put("energyType", energyType);
    resp.put("annualKWh", kwh);
    resp.put("unitPrice", prixKwh);
    resp.put("subscriptionMonthly", abonnement);
    resp.put("totalYear", total);
    return ResponseEntity.ok(resp);
  }
}
