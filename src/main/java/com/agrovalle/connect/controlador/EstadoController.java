package com.agrovalle.connect.controlador;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint de salud público para verificar el despliegue (por ejemplo, en Staging). */
@RestController
public class EstadoController {

    private final DataSource dataSource;

    private final Environment environment;

    public EstadoController(DataSource dataSource, Environment environment) {
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @GetMapping("/api/v1/estado")
    public ResponseEntity<Map<String, String>> estado() {
        String[] perfiles = environment.getActiveProfiles();
        Map<String, String> cuerpo = new LinkedHashMap<>();
        cuerpo.put("aplicacion", "AgroValle Connect");
        cuerpo.put("perfil", perfiles.length == 0 ? "default" : String.join(",", perfiles));
        try (Connection conexion = dataSource.getConnection()) {
            cuerpo.put("estado", "OK");
            cuerpo.put("baseDatos", conexion.getMetaData().getDatabaseProductName() + " Connected");
            return ResponseEntity.ok(cuerpo);
        } catch (SQLException e) {
            cuerpo.put("estado", "ERROR");
            cuerpo.put("baseDatos", "Database Unavailable");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(cuerpo);
        }
    }
}
