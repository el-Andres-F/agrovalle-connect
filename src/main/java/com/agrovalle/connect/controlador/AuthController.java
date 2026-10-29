package com.agrovalle.connect.controlador;

import com.agrovalle.connect.dto.LoginRequest;
import com.agrovalle.connect.dto.PerfilActualizacionRequest;
import com.agrovalle.connect.dto.PerfilResponse;
import com.agrovalle.connect.dto.RegistroRequest;
import com.agrovalle.connect.dto.RegistroResponse;
import com.agrovalle.connect.dto.SesionResponse;
import com.agrovalle.connect.dto.TipoUsuario;
import com.agrovalle.connect.modelo.Administrador;
import com.agrovalle.connect.modelo.Comprador;
import com.agrovalle.connect.repositorio.AdministradorRepository;
import com.agrovalle.connect.repositorio.CompradorRepository;
import com.agrovalle.connect.servicio.RegistroService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern CONTRASENA = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");
    private static final Pattern TELEFONO = Pattern.compile("^3\\d{9}$");

    private final RegistroService registroService;
    private final AdministradorRepository administradorRepository;
    private final CompradorRepository compradorRepository;
    private final BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();

    public AuthController(RegistroService registroService,
            AdministradorRepository administradorRepository,
            CompradorRepository compradorRepository) {
        this.registroService = registroService;
        this.administradorRepository = administradorRepository;
        this.compradorRepository = compradorRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistroResponse> register(@RequestBody RegistroRequest solicitud) {
        if (solicitud == null || solicitud.tipo() != TipoUsuario.COMPRADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El registro público está disponible únicamente para compradores.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(registroService.registrar(solicitud));
    }

    @PostMapping("/login")
    public SesionResponse login(@RequestBody LoginRequest solicitud, HttpServletRequest request) {
        if (solicitud == null || solicitud.usuario() == null || solicitud.contrasena() == null) {
            throw credencialesInvalidas();
        }
        String tipo = solicitud.tipo() == null ? "AUTO" : solicitud.tipo().trim().toUpperCase(Locale.ROOT);
        String nombre;
        Long id;
        Administrador administrador = administradorRepository.findByUsuario(solicitud.usuario().trim())
                .filter(cuenta -> codificador.matches(solicitud.contrasena(), cuenta.getContrasenaHash()))
                .orElse(null);
        if ("ADMIN".equals(tipo) || ("AUTO".equals(tipo) && administrador != null)) {
            if (administrador == null) {
                throw credencialesInvalidas();
            }
            Administrador admin = administrador;
            id = admin.getId();
            nombre = nombreCompleto(admin.getNombre(), admin.getApellido(), admin.getUsuario());
            tipo = "ADMIN";
        } else if ("COMPRADOR".equals(tipo) || "AUTO".equals(tipo)) {
            Comprador comprador = compradorRepository.findByCorreo(
                            solicitud.usuario().trim().toLowerCase(Locale.ROOT))
                    .filter(cuenta -> codificador.matches(solicitud.contrasena(), cuenta.getContrasenaHash()))
                    .orElseThrow(AuthController::credencialesInvalidas);
            id = comprador.getId();
            nombre = nombreCompleto(comprador.getNombre(), comprador.getApellido(), comprador.getNombre());
            tipo = "COMPRADOR";
        } else {
            throw credencialesInvalidas();
        }

        HttpSession previous = request.getSession(false);
        if (previous != null) {
            previous.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("usuarioId", id);
        session.setAttribute("tipoUsuario", tipo);
        session.setAttribute("nombreUsuario", nombre);
        return new SesionResponse(true, tipo, nombre);
    }

    @GetMapping("/session")
    public SesionResponse session(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("tipoUsuario") == null) {
            return new SesionResponse(false, null, null);
        }
        return new SesionResponse(true,
                (String) session.getAttribute("tipoUsuario"),
                (String) session.getAttribute("nombreUsuario"));
    }

    @GetMapping("/profile")
    public PerfilResponse obtenerPerfil(HttpServletRequest request) {
        HttpSession session = exigirSesion(request);
        String tipo = (String) session.getAttribute("tipoUsuario");
        Long id = (Long) session.getAttribute("usuarioId");
        if ("ADMIN".equals(tipo)) {
            Administrador admin = administradorRepository.findById(id).orElseThrow(AuthController::sesionInvalida);
            return new PerfilResponse(tipo, admin.getNombre(), admin.getApellido(), admin.getCorreo(), null);
        }
        Comprador comprador = compradorRepository.findById(id).orElseThrow(AuthController::sesionInvalida);
        return new PerfilResponse(tipo, comprador.getNombre(), comprador.getApellido(),
                comprador.getCorreo(), comprador.getTelefono());
    }

    @PostMapping("/profile")
    public PerfilResponse actualizarPerfil(@RequestBody PerfilActualizacionRequest datos,
            HttpServletRequest request) {
        HttpSession session = exigirSesion(request);
        String tipo = (String) session.getAttribute("tipoUsuario");
        Long id = (Long) session.getAttribute("usuarioId");
        exigirTexto(datos.nombre(), "nombre");
        exigirTexto(datos.apellido(), "apellido");
        if (datos.correo() == null || !CORREO.matcher(datos.correo().trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no tiene un formato válido.");
        }
        String correo = datos.correo().trim().toLowerCase(Locale.ROOT);
        validarCambioContrasena(datos.contrasenaActual(), datos.contrasenaNueva());
        if ("ADMIN".equals(tipo)) {
            Administrador admin = administradorRepository.findById(id).orElseThrow(AuthController::sesionInvalida);
            if (administradorRepository.existsByCorreoAndIdNot(correo, id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está registrado.");
            }
            admin.setNombre(datos.nombre().trim());
            admin.setApellido(datos.apellido().trim());
            admin.setCorreo(correo);
            actualizarContrasena(datos, admin.getContrasenaHash(), admin::setContrasenaHash);
            administradorRepository.save(admin);
            actualizarNombreSesion(session, admin.getNombre(), admin.getApellido());
            return new PerfilResponse(tipo, admin.getNombre(), admin.getApellido(), admin.getCorreo(), null);
        }
        Comprador comprador = compradorRepository.findById(id).orElseThrow(AuthController::sesionInvalida);
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
        actualizarNombreSesion(session, comprador.getNombre(), comprador.getApellido());
        return new PerfilResponse(tipo, comprador.getNombre(), comprador.getApellido(),
                comprador.getCorreo(), comprador.getTelefono());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    private static ResponseStatusException credencialesInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos.");
    }

    private HttpSession exigirSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("tipoUsuario") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inicia sesión para continuar.");
        }
        return session;
    }

    private void validarCambioContrasena(String actual, String nueva) {
        if (nueva != null && !nueva.isBlank()
                && (actual == null || actual.isBlank() || !CONTRASENA.matcher(nueva).matches())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Para cambiar la contraseña, indica la actual y usa una nueva de 8 caracteres con letras y números.");
        }
    }

    private void actualizarContrasena(PerfilActualizacionRequest datos, String hash,
            java.util.function.Consumer<String> actualizar) {
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

    private void actualizarNombreSesion(HttpSession session, String nombre, String apellido) {
        session.setAttribute("nombreUsuario", nombreCompleto(nombre, apellido, nombre));
    }

    private String nombreCompleto(String nombre, String apellido, String fallback) {
        String resultado = ((nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido)).trim();
        return resultado.isBlank() ? fallback : resultado;
    }

    private static ResponseStatusException sesionInvalida() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La cuenta de la sesión ya no existe.");
    }
}
