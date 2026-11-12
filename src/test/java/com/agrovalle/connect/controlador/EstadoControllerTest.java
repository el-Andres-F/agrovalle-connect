package com.agrovalle.connect.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;

class EstadoControllerTest {

    @Test
    void reportaPerfilActivoYBaseDeDatosConectada() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection conexion = mock(Connection.class);
        DatabaseMetaData metadatos = mock(DatabaseMetaData.class);
        when(dataSource.getConnection()).thenReturn(conexion);
        when(conexion.getMetaData()).thenReturn(metadatos);
        when(metadatos.getDatabaseProductName()).thenReturn("PostgreSQL");
        MockEnvironment entorno = new MockEnvironment();
        entorno.setActiveProfiles("staging");

        ResponseEntity<Map<String, String>> respuesta = new EstadoController(dataSource, entorno).estado();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("AgroValle Connect", respuesta.getBody().get("aplicacion"));
        assertEquals("OK", respuesta.getBody().get("estado"));
        assertEquals("staging", respuesta.getBody().get("perfil"));
        assertEquals("PostgreSQL Connected", respuesta.getBody().get("baseDatos"));
    }

    @Test
    void respondeServicioNoDisponibleSiLaBaseDeDatosFalla() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenThrow(new SQLException("sin conexion"));

        ResponseEntity<Map<String, String>> respuesta =
                new EstadoController(dataSource, new MockEnvironment()).estado();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, respuesta.getStatusCode());
        assertEquals("ERROR", respuesta.getBody().get("estado"));
        assertEquals("default", respuesta.getBody().get("perfil"));
        assertEquals("Database Unavailable", respuesta.getBody().get("baseDatos"));
    }
}
