package com.agrovalle.connect.servicio;

import com.agrovalle.connect.dto.LoginRequest;
import com.agrovalle.connect.dto.PerfilActualizacionRequest;
import com.agrovalle.connect.dto.PerfilResponse;
import com.agrovalle.connect.modelo.Administrador;
import com.agrovalle.connect.modelo.Comprador;
import com.agrovalle.connect.repositorio.AdministradorRepository;
import com.agrovalle.connect.repositorio.CompradorRepository;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Authentication and profile use cases; repositories remain outside REST adapters. */
@Service
public class AutenticacionService {

    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern CONTRASENA = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");
    private static final Pattern TELEFONO = Pattern.compile("^3\\d{9}$");

    private final AdministradorRepository administradorRepository;
    private final CompradorRepository compradorRepository;
    private final BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();

    public AutenticacionService(AdministradorRepository administradorRepository,
            CompradorRepository compradorRepository) {
        this.administradorRepository = administradorRepository;
        this.compradorRepository = compradorRepository;
    }

    @Transactional(readOnly = true)
    public UsuarioAutenticado autenticar(LoginRequest solicitud) {
        if (solicitud == null || solicitud.usuario() == null || solicitud.contrasena() == null) {
            throw credencialesInvalidas();
        }
        String tipo = solicitud.tipo() == null ? "AUTO" : solicitud.tipo().trim().toUpperCase(Locale.ROOT);
        Administrador administrador = administradorRepository.findByUsuario(solicitud.usuario().trim())
                .filter(cuenta -> codificador.matches(solicitud.contrasena(), cuenta.getContrasenaHash()))
                .orElse(null);
        if ("ADMIN".equals(tipo) || ("AUTO".equals(tipo) && administrador != null)) {
            if (administrador == null) {
                throw credencialesInvalidas();
            }
            String nombre = nombreCompleto(administrador.getNombre(), administrador.getApellido(),
                    administrador.getUsuario());
            return new UsuarioAutenticado("ADMIN", administrador.getId(), nombre);
        }
        if ("COMPRADOR".equals(tipo) || "AUTO".equals(tipo)) {
            Comprador comprador = compradorRepository.findByCorreo(
                            solicitud.usuario().trim().toLowerCase(Locale.ROOT))
                    .filter(cuenta -> codificador.matches(solicitud.contrasena(), cuenta.getContrasenaHash()))
                    .orElseThrow(AutenticacionService::credencialesInvalidas);
            String nombre = nombreCompleto(comprador.getNombre(), comprador.getApellido(), comprador.getNombre());
            return new UsuarioAutenticado("COMPRADOR", comprador.getId(), nombre);
        }
        throw credencialesInvalidas();
    }

    @Transactional(readOnly = true)
    public PerfilResponse obtenerPerfil(String tipo, Long id) {
        if ("ADMIN".equals(tipo)) {
            Administrador admin = administradorRepository.findById(id)
                .orElseThrow(AutenticacionService::sesionInvalida);
            return new PerfilResponse(tipo, admin.getNombre(), admin.getApellido(), admin.getCorreo(), null);
        }
        Comprador comprador = compradorRepository.findById(id).orElseThrow(AutenticacionService::sesionInvalida);
        return new PerfilResponse(tipo, comprador.getNombre(), comprador.getApellido(),
                comprador.getCorreo(), comprador.getTelefono());
    }

    @Transactional
    public PerfilResponse actualizarPerfil(String tipo, Long id, PerfilActualizacionRequest datos) {
        if (datos == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los datos del perfil son obligatorios.");
        }
        exigirTexto(datos.nombre(), "nombre");
        exigirTexto(datos.apellido(), "apellido");
        if (datos.correo() == null || !CORREO.matcher(datos.correo().trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no tiene un formato válido.");
        }
        String correo = datos.correo().trim().toLowerCase(Locale.ROOT);
        validarCambioContrasena(datos.contrasenaActual(), datos.contrasenaNueva());
        if ("ADMIN".equals(tipo)) {
            return actualizarAdministrador(id, datos, correo);
        }
        return actualizarComprador(id, datos, correo);
    }

    private PerfilResponse actualizarAdministrador(Long id, PerfilActualizacionRequest datos, String correo) {
        Administrador admin = administradorRepository.findById(id).orElseThrow(AutenticacionService::sesionInvalida);
        if (administradorRepository.existsByCorreoAndIdNot(correo, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está registrado.");
        }
        admin.setNombre(datos.nombre().trim());
        admin.setApellido(datos.apellido().trim());
        admin.setCorreo(correo);
        actualizarContrasena(datos, admin.getContrasenaHash(), admin::setContrasenaHash);
        administradorRepository.save(admin);
        return new PerfilResponse("ADMIN", admin.getNombre(), admin.getApellido(), admin.getCorreo(), null);
    }

    private PerfilResponse actualizarComprador(Long id, PerfilActualizacionRequest datos, String correo) {
        Comprador comprador = compradorRepository.findById(id).orElseThrow(AutenticacionService::sesionInvalida);
        if (compradorRepository.existsByCorreoAndIdNot(correo, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está registrado.");
        }
        if (datos.telefono() == null || !TELEFONO.matcher(datos.telefono()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El teléfono debe ser un celular colombiano de 10 dígitos.");
        }
        comprador.setNombre(datos.nombre().trim());
        comprador.setApellido(datos.apellido().trim());
        comprador.setCorreo(correo);
        comprador.setTelefono(datos.telefono());
        actualizarContrasena(datos, comprador.getContrasenaHash(), comprador::setContrasenaHash);
        compradorRepository.save(comprador);
        return new PerfilResponse("COMPRADOR", comprador.getNombre(), comprador.getApellido(),
                comprador.getCorreo(), comprador.getTelefono());
    }

    private void validarCambioContrasena(String actual, String nueva) {
        if (nueva != null && !nueva.isBlank()
                && (actual == null || actual.isBlank() || !CONTRASENA.matcher(nueva).matches())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Para cambiar la contraseña, indica la actual y usa una nueva "
                            + "de 8 caracteres con letras y números.");
        }
    }

    private void actualizarContrasena(PerfilActualizacionRequest datos, String hash, Consumer<String> actualizar) {
        if (datos.contrasenaNueva() == null || datos.contrasenaNueva().isBlank()) {
            return;
        }
        if (!codificador.matches(datos.contrasenaActual(), hash)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual no coincide.");
        }
        actualizar.accept(codificador.encode(datos.contrasenaNueva()));
    }

    private void exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo " + campo + " es obligatorio.");
        }
    }

    private String nombreCompleto(String nombre, String apellido, String fallback) {
        String resultado = ((nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido)).trim();
        return resultado.isBlank() ? fallback : resultado;
    }

    private static ResponseStatusException credencialesInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos.");
    }

    private static ResponseStatusException sesionInvalida() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La cuenta de la sesión ya no existe.");
    }

    public record UsuarioAutenticado(String tipo, Long id, String nombre) {
    }
}
