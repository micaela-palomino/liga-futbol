package com.uade.ligafutbol.controller;

import com.uade.ligafutbol.model.Partido;
import com.uade.ligafutbol.service.LigaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partidos")
@CrossOrigin(origins = "*")
public class PartidoController {
    
    @Autowired
    private LigaService ligaService;
    
    @PostMapping
    public ResponseEntity<Partido> crearPartido(@RequestBody Partido partido) {
        Partido nuevoPartido = ligaService.crearPartido(partido);
        return ResponseEntity.ok(nuevoPartido);
    }
    
    @GetMapping
    public ResponseEntity<List<Partido>> obtenerTodosLosPartidos() {
        List<Partido> partidos = ligaService.obtenerTodosLosPartidos();
        return ResponseEntity.ok(partidos);
    }
    
    @PutMapping("/{id}/resultado")
    public ResponseEntity<Partido> registrarResultado(
            @PathVariable String id,
            @RequestParam Integer golesLocal,
            @RequestParam Integer golesVisitante) {
        Partido partido = ligaService.registrarResultado(id, golesLocal, golesVisitante);
        if (partido != null) {
            return ResponseEntity.ok(partido);
        }
        return ResponseEntity.notFound().build();
    }
}
