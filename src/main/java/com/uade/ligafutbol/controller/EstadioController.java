package com.uade.ligafutbol.controller;

import com.uade.ligafutbol.model.Estadio;
import com.uade.ligafutbol.service.LigaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estadios")
@CrossOrigin(origins = "*")
public class EstadioController {
    
    @Autowired
    private LigaService ligaService;
    
    @PostMapping
    public ResponseEntity<Estadio> crearEstadio(@RequestBody Estadio estadio) {
        Estadio nuevoEstadio = ligaService.crearEstadio(estadio);
        return ResponseEntity.ok(nuevoEstadio);
    }
    
    @GetMapping
    public ResponseEntity<List<Estadio>> obtenerTodosLosEstadios() {
        List<Estadio> estadios = ligaService.obtenerTodosLosEstadios();
        return ResponseEntity.ok(estadios);
    }
}
