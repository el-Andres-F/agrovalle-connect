package com.agrovalle.connect.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.dto.RegistroRequest;
import com.agrovalle.connect.dto.TipoUsuario;
import com.agrovalle.connect.excepcion.RegistroDuplicadoException;
import com.agrovalle.connect.excepcion.RegistroInvalidoException;
import com.agrovalle.connect.modelo.Productor;
import com.agrovalle.connect.modelo.Comprador;
import com.agrovalle.connect.repositorio.CompradorRepository;
import com.agrovalle.connect.repositorio.ProductorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistroServiceTest {

    @Mock
    private ProductorRepository productorRepository;

    @Mock
    private CompradorRepository compradorRepository;

    @InjectMocks
    private RegistroService registroService;

    @Test
    void registroProductorValidoPersisteYDevuelveRespuesta() {
        when(productorRepository.existsByCedula("12345678")).thenReturn(false);
        when(productorRepository.save(any(Productor.class))).thenAnswer(invocation -> {
            Productor productor = invocation.getArgument(0);
            productor.setId(42L);
            return productor;
        });
        RegistroRequest datos = new RegistroRequest(TipoUsuario.PRODUCTOR, "Ana", "Palmira",
            "12345678", null, null, null, null, null);

        var respuesta = registroService.registrar(datos);

        assertEquals(42L, respuesta.id());
        assertEquals(TipoUsuario.PRODUCTOR, respuesta.tipo());
        verify(productorRepository).save(any(Productor.class));
    }

    @Test
    void cedulaInvalidaFallaAntesDeConsultarRepositorio() {
        RegistroRequest datos = new RegistroRequest(TipoUsuario.PRODUCTOR, "Ana", "Palmira",
            "12A", null, null, null, null, null);

        assertThrows(RegistroInvalidoException.class, () -> registroService.registrar(datos));

        verify(productorRepository, never()).existsByCedula(any());
    }

    @Test
    void cedulaDuplicadaEsRechazada() {
        when(productorRepository.existsByCedula("12345678")).thenReturn(true);
        RegistroRequest datos = new RegistroRequest(TipoUsuario.PRODUCTOR, "Ana", "Palmira",
            "12345678", null, null, null, null, null);

        assertThrows(RegistroDuplicadoException.class, () -> registroService.registrar(datos));

        verify(productorRepository, never()).save(any(Productor.class));
    }

    @Test
    void registroNuloEsRechazado() {
        assertThrows(RegistroInvalidoException.class, () -> registroService.registrar(null));
        verify(productorRepository, never()).save(any(Productor.class));
    }

    @Test
    void registroCompradorValidoGuardaHashDeContrasena() {
        when(compradorRepository.existsByCorreo("ana@example.com")).thenReturn(false);
        when(compradorRepository.save(any(Comprador.class))).thenAnswer(invocation -> invocation.getArgument(0));
        RegistroRequest datos = new RegistroRequest(TipoUsuario.COMPRADOR, "Ana", null,
                null, "ANA@example.com", "3001234567", "segura123", " Apellido ", null);

        var respuesta = registroService.registrar(datos);

        assertEquals(TipoUsuario.COMPRADOR, respuesta.tipo());
        verify(compradorRepository).save(any(Comprador.class));
    }

    @Test
    void correoInvalidoSeRechazaAntesDeGuardar() {
        RegistroRequest datos = new RegistroRequest(TipoUsuario.COMPRADOR, "Ana", null,
                null, "correo-invalido", "3001234567", "segura123", null, null);

        assertThrows(RegistroInvalidoException.class, () -> registroService.registrar(datos));

        verify(compradorRepository, never()).save(any(Comprador.class));
    }

    @Test
    void registroPublicoNoPermiteTipoAdministrador() {
        RegistroRequest datos = new RegistroRequest(TipoUsuario.ADMIN, "Admin", null,
                null, null, null, null, null, null);

        assertThrows(RegistroInvalidoException.class, () -> registroService.registrar(datos));
    }

    @Test
    void productorSinNombreEsRechazado() {
        RegistroRequest datos = new RegistroRequest(TipoUsuario.PRODUCTOR, " ", "Palmira",
                "12345678", null, null, null, null, null);

        assertThrows(RegistroInvalidoException.class, () -> registroService.registrar(datos));

        verify(productorRepository, never()).save(any(Productor.class));
    }

    @Test
    void compradorConTelefonoInvalidoEsRechazado() {
        RegistroRequest datos = new RegistroRequest(TipoUsuario.COMPRADOR, "Ana", null,
                null, "ana@example.com", "12345", "segura123", null, null);

        assertThrows(RegistroInvalidoException.class, () -> registroService.registrar(datos));

        verify(compradorRepository, never()).save(any(Comprador.class));
    }

    @Test
    void compradorConContrasenaDebilEsRechazado() {
        RegistroRequest datos = new RegistroRequest(TipoUsuario.COMPRADOR, "Ana", null,
                null, "ana@example.com", "3001234567", "12345678", null, null);

        assertThrows(RegistroInvalidoException.class, () -> registroService.registrar(datos));

        verify(compradorRepository, never()).save(any(Comprador.class));
    }

    @Test
    void correoDeCompradorDuplicadoEsRechazado() {
        when(compradorRepository.existsByCorreo("ana@example.com")).thenReturn(true);
        RegistroRequest datos = new RegistroRequest(TipoUsuario.COMPRADOR, "Ana", null,
                null, "ANA@example.com", "3001234567", "segura123", null, null);

        assertThrows(RegistroDuplicadoException.class, () -> registroService.registrar(datos));

        verify(compradorRepository, never()).save(any(Comprador.class));
    }
}
