
package com.example.demo;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerDemo {

    // Almacenamiento temporal de plantas medicinales
    private final Map<Long, Map<String, Object>> plantas =
            new ConcurrentHashMap<>();

    // Generador automático de identificadores
    private final AtomicLong contador = new AtomicLong(0);

    // MÉTODO GET: Consultar una planta medicinal
    @GetMapping("/plantas/{id}")
    public ResponseEntity<Map<String, Object>> consultarPlanta(
            @PathVariable Long id) {

        Map<String, Object> planta = plantas.get(id);

        if (planta == null) {
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Planta medicinal no encontrada");

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(respuesta);
        }

        return ResponseEntity.ok(planta);
    }

    // MÉTODO POST: Registrar una planta medicinal
    @PostMapping("/plantas/nueva")
    public ResponseEntity<Map<String, Object>> registrarPlanta(
            @RequestBody Map<String, Object> nueva) {

        String[] campos = {
            "nombre",
            "habitat",
            "parteUtilizada",
            "propiedadTradicional"
        };

        // Validar que los cuatro campos estén completos
        for (String campo : campos) {
            Object valor = nueva.get(campo);

            if (!(valor instanceof String) ||
                    ((String) valor).isBlank()) {

                Map<String, Object> error = new HashMap<>();
                error.put("error", "El campo '" + campo
                        + "' es obligatorio y debe ser texto");

                return ResponseEntity.badRequest().body(error);
            }
        }

        // Crear el objeto de la planta medicinal
        Long id = contador.incrementAndGet();

        Map<String, Object> planta = new LinkedHashMap<>();
        planta.put("id", id);
        planta.put("nombre", nueva.get("nombre"));
        planta.put("habitat", nueva.get("habitat"));
        planta.put("parteUtilizada", nueva.get("parteUtilizada"));
        planta.put("propiedadTradicional",
                nueva.get("propiedadTradicional"));

        // Guardar la planta en memoria
        plantas.put(id, planta);

        return ResponseEntity.status(HttpStatus.CREATED).body(planta);
    }
}
