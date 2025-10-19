package com.uade.ligafutbol.config;

import com.uade.ligafutbol.model.*;
import com.uade.ligafutbol.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Inicializa la base de datos con datos de ejemplo
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private EquipoRepository equipoRepository;
    
    @Autowired
    private EstadioRepository estadioRepository;
    
    @Autowired
    private PartidoRepository partidoRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Limpiar datos existentes (opcional)
        // equipoRepository.deleteAll();
        // estadioRepository.deleteAll();
        // partidoRepository.deleteAll();
        
        // Verificar si ya hay datos
        if (equipoRepository.count() > 0) {
            System.out.println("La base de datos ya contiene datos. Omitiendo inicialización.");
            return;
        }
        
        System.out.println("Inicializando base de datos con datos de ejemplo...");
        
        // Crear estadios
        Estadio bombonera = new Estadio("La Bombonera", "Buenos Aires", 54000, -34.6354, -58.3648);
        Estadio monumental = new Estadio("El Monumental", "Buenos Aires", 83000, -34.5451, -58.4498);
        Estadio cilindro = new Estadio("El Cilindro", "Avellaneda", 51000, -34.6656, -58.3719);
        Estadio libertadores = new Estadio("Libertadores de América", "Avellaneda", 52000, -34.6673, -58.3686);
        
        estadioRepository.save(bombonera);
        estadioRepository.save(monumental);
        estadioRepository.save(cilindro);
        estadioRepository.save(libertadores);
        
        // Crear conexiones entre estadios
        ConexionEstadio conn1 = new ConexionEstadio(monumental, 12.5, 500.0, 30);
        ConexionEstadio conn2 = new ConexionEstadio(cilindro, 8.3, 350.0, 25);
        ConexionEstadio conn3 = new ConexionEstadio(libertadores, 8.5, 360.0, 25);
        
        bombonera.getConexiones().add(conn1);
        bombonera.getConexiones().add(conn2);
        bombonera.getConexiones().add(conn3);
        
        ConexionEstadio conn4 = new ConexionEstadio(bombonera, 12.5, 500.0, 30);
        ConexionEstadio conn5 = new ConexionEstadio(cilindro, 15.2, 600.0, 35);
        
        monumental.getConexiones().add(conn4);
        monumental.getConexiones().add(conn5);
        
        estadioRepository.save(bombonera);
        estadioRepository.save(monumental);
        
        // Crear equipos
        Equipo boca = new Equipo("Boca Juniors", "Buenos Aires");
        boca.setEstadio(bombonera);
        
        Equipo river = new Equipo("River Plate", "Buenos Aires");
        river.setEstadio(monumental);
        
        Equipo racing = new Equipo("Racing Club", "Avellaneda");
        racing.setEstadio(cilindro);
        
        Equipo independiente = new Equipo("Independiente", "Avellaneda");
        independiente.setEstadio(libertadores);
        
        // Crear conexiones entre equipos
        ConexionEquipo connEq1 = new ConexionEquipo(river, 12.5, 500.0, 30);
        ConexionEquipo connEq2 = new ConexionEquipo(racing, 8.3, 350.0, 25);
        ConexionEquipo connEq3 = new ConexionEquipo(independiente, 8.5, 360.0, 25);
        
        boca.getConexiones().add(connEq1);
        boca.getConexiones().add(connEq2);
        boca.getConexiones().add(connEq3);
        
        ConexionEquipo connEq4 = new ConexionEquipo(boca, 12.5, 500.0, 30);
        ConexionEquipo connEq5 = new ConexionEquipo(racing, 15.2, 600.0, 35);
        ConexionEquipo connEq6 = new ConexionEquipo(independiente, 15.5, 610.0, 36);
        
        river.getConexiones().add(connEq4);
        river.getConexiones().add(connEq5);
        river.getConexiones().add(connEq6);
        
        ConexionEquipo connEq7 = new ConexionEquipo(boca, 8.3, 350.0, 25);
        ConexionEquipo connEq8 = new ConexionEquipo(river, 15.2, 600.0, 35);
        ConexionEquipo connEq9 = new ConexionEquipo(independiente, 2.1, 100.0, 10);
        
        racing.getConexiones().add(connEq7);
        racing.getConexiones().add(connEq8);
        racing.getConexiones().add(connEq9);
        
        ConexionEquipo connEq10 = new ConexionEquipo(boca, 8.5, 360.0, 25);
        ConexionEquipo connEq11 = new ConexionEquipo(river, 15.5, 610.0, 36);
        ConexionEquipo connEq12 = new ConexionEquipo(racing, 2.1, 100.0, 10);
        
        independiente.getConexiones().add(connEq10);
        independiente.getConexiones().add(connEq11);
        independiente.getConexiones().add(connEq12);
        
        equipoRepository.save(boca);
        equipoRepository.save(river);
        equipoRepository.save(racing);
        equipoRepository.save(independiente);
        
        // Crear algunos partidos de ejemplo
        LocalDateTime fecha1 = LocalDateTime.now().plusDays(7);
        LocalDateTime fecha2 = LocalDateTime.now().plusDays(14);
        LocalDateTime fecha3 = LocalDateTime.now().plusDays(21);
        
        Partido partido1 = new Partido(boca, river, bombonera, fecha1, 1);
        Partido partido2 = new Partido(racing, independiente, cilindro, fecha1, 1);
        Partido partido3 = new Partido(river, racing, monumental, fecha2, 2);
        Partido partido4 = new Partido(independiente, boca, libertadores, fecha2, 2);
        
        partidoRepository.save(partido1);
        partidoRepository.save(partido2);
        partidoRepository.save(partido3);
        partidoRepository.save(partido4);
        
        // Simular algunos resultados
        partido1.registrarResultado(2, 1);
        partido2.registrarResultado(1, 1);
        
        equipoRepository.save(boca);
        equipoRepository.save(river);
        equipoRepository.save(racing);
        equipoRepository.save(independiente);
        
        partidoRepository.save(partido1);
        partidoRepository.save(partido2);
        
        System.out.println("✅ Base de datos inicializada con éxito!");
        System.out.println("📊 Equipos creados: " + equipoRepository.count());
        System.out.println("🏟️  Estadios creados: " + estadioRepository.count());
        System.out.println("⚽ Partidos creados: " + partidoRepository.count());
    }
}
