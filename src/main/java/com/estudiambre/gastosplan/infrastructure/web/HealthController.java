package com.estudiambre.gastosplan.infrastructure.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Sin autenticacion ni certificado: para comprobar que el servicio esta arriba. */
@RestController
public class HealthController {
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "ms-gastos-plan");
    }
}
