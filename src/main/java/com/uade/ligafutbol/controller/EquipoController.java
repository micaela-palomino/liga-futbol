package com.uade.ligafutbol.controller;

import com.uade.ligafutbol.model.Equipo;
import com.uade.ligafutbol.service.LigaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*")
public class EquipoController {
    
    @Autowired
    private LigaService ligaService;
    
    @PostMapping
    public ResponseEntity<Equipo> crearEquipo(@RequestBody Equipo equipo) {
        Equipo nuevoEquipo = ligaService.crearEquipo(equipo);
        return ResponseEntity.ok(nuevoEquipo);
    }
    
    @GetMapping
    public ResponseEntity<List<Equipo>> obtenerTodosLosEquipos() {
        List<Equipo> equipos = ligaService.obtenerTodosLosEquipos();
        return ResponseEntity.ok(equipos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Equipo> obtenerEquipoPorId(@PathVariable Long id) {
        Equipo equipo = ligaService.obtenerEquipoPorId(id);
        if (equipo != null) {
            return ResponseEntity.ok(equipo);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/tabla-posiciones")
    public ResponseEntity<List<Equipo>> obtenerTablaPosiciones() {
        List<Equipo> tabla = ligaService.obtenerTablaPosiciones();
        return ResponseEntity.ok(tabla);
    }
}
