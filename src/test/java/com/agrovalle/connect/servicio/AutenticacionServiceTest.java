package com.agrovalle.connect.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.dto.LoginRequest;
import com.agrovalle.connect.dto.PerfilActualizacionRequest;
import com.agrovalle.connect.modelo.Administrador;
import com.agrovalle.connect.modelo.Comprador;
import com.agrovalle.connect.repositorio.AdministradorRepository;
import com.agrovalle.connect.repositorio.CompradorRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AutenticacionServiceTest {

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private CompradorRepository compradorRepository;

    @InjectMocks
    private AutenticacionService autenticacionService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private Administrador administrador;
    private Comprador comprador;

    @BeforeEach
    void prepararCuentas() {
        administrador = new Administrador();
        administrador.setUsuario("admin");
        administrador.setNombre("Ana");
        administrador.setApellido("Admin");
        administrador.setContrasenaHash(encoder.encode("clave123"));
        comprador = new Comprador();
        comprador.setNombre("Luis");
        comprador.setApellido("Comprador");
        comprador.setCorreo("luis@example.com");
        comprador.setTelefono("3001234567");
        comprador.setContrasenaHash(encoder.encode("clave123"));
    }

    @Test
    void autenticaAdministradorYSeleccionAutomatica() {
        when(administradorRepository.findByUsuario("admin")).thenReturn(Optional.of(administrador));

        var user = autenticacionService.autenticar(new LoginRequest("admin", "clave123", "AUTO"));

        assertEquals("ADMIN", user.tipo());
        assertEquals("Ana Admin", user.nombre());
    }

    @Test
    void autenticaCompradorPorCorreoNormalizado() {
        when(administradorRepository.findByUsuario("LUIS@example.com")).thenReturn(Optional.empty());
        when(compradorRepository.findByCorreo("luis@example.com")).thenReturn(Optional.of(comprador));

        var user = autenticacionService.autenticar(new LoginRequest("LUIS@example.com", "clave123", "AUTO"));

        assertEquals("COMPRADOR", user.tipo());
        assertEquals("Luis Comprador", user.nombre());
    }

    @Test
    void credencialesIncorrectasSeRechazan() {
        when(administradorRepository.findByUsuario("admin")).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> autenticacionService.autenticar(new LoginRequest("admin", "clave123", "ADMIN")));

        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
    }

    @Test
    void solicitudDeLoginNulaSeRechaza() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> autenticacionService.autenticar(null));
        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
    }

    @Test
    void consultaPerfilDeCompradorYCuentaInexistente() {
        when(compradorRepository.findById(4L)).thenReturn(Optional.of(comprador), Optional.empty());

        assertEquals("luis@example.com", autenticacionService.obtenerPerfil("COMPRADOR", 4L).correo());
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> autenticacionService.obtenerPerfil("COMPRADOR", 4L));
        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
    }

    @Test
    void actualizaPerfilDeCompradorYHashCuandoSeCambiaContrasena() {
        when(compradorRepository.findById(4L)).thenReturn(Optional.of(comprador));
        when(compradorRepository.existsByCorreoAndIdNot("nuevo@example.com", 4L)).thenReturn(false);
        PerfilActualizacionRequest request = new PerfilActualizacionRequest("Nuevo", "Nombre", "NUEVO@example.com",
                "3009876543", "clave123", "nueva1234");

        var profile = autenticacionService.actualizarPerfil("COMPRADOR", 4L, request);

        assertEquals("nuevo@example.com", profile.correo());
        assertEquals("3009876543", profile.telefono());
        verify(compradorRepository).save(comprador);
    }

    @Test
    void correoDuplicadoEnPerfilSeRechaza() {
        when(compradorRepository.findById(4L)).thenReturn(Optional.of(comprador));
        when(compradorRepository.existsByCorreoAndIdNot("luis@example.com", 4L)).thenReturn(true);
        PerfilActualizacionRequest request = new PerfilActualizacionRequest("Luis", "Comprador",
                "luis@example.com", "3001234567", null, null);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> autenticacionService.actualizarPerfil("COMPRADOR", 4L, request));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(compradorRepository, never()).save(any(Comprador.class));
    }

    @Test
    void telefonoInvalidoSeRechaza() {
        when(compradorRepository.findById(4L)).thenReturn(Optional.of(comprador));
        when(compradorRepository.existsByCorreoAndIdNot("nuevo@example.com", 4L)).thenReturn(false);
        PerfilActualizacionRequest request = new PerfilActualizacionRequest("Luis", "Comprador",
                "nuevo@example.com", "123", null, null);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> autenticacionService.actualizarPerfil("COMPRADOR", 4L, request));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void actualizaPerfilAdministrador() {
        when(administradorRepository.findById(2L)).thenReturn(Optional.of(administrador));
        when(administradorRepository.existsByCorreoAndIdNot("nuevo@example.com", 2L)).thenReturn(false);
        PerfilActualizacionRequest request = new PerfilActualizacionRequest("Ana", "Nueva", "nuevo@example.com",
                null, null, null);

        var profile = autenticacionService.actualizarPerfil("ADMIN", 2L, request);

        assertEquals("ADMIN", profile.tipo());
        verify(administradorRepository).save(administrador);
    }

    @Test
    void datosInvalidosDePerfilSeRechazan() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> autenticacionService.actualizarPerfil("COMPRADOR", 4L,
                        new PerfilActualizacionRequest("", "", "bad", "3001234567", null, null)));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }
}
