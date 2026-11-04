package com.agrovalle.connect.controlador;

import com.agrovalle.connect.dto.PerfilActualizacionRequest;
import com.agrovalle.connect.dto.PerfilResponse;
import com.agrovalle.connect.dto.RegistroRequest;
import com.agrovalle.connect.dto.RegistroResponse;
import com.agrovalle.connect.dto.SesionResponse;
import com.agrovalle.connect.dto.TipoUsuario;
import com.agrovalle.connect.servicio.AutenticacionService;
import com.agrovalle.connect.servicio.RegistroService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** REST adapter for public registration, session, authentication and profile endpoints. */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegistroService registroService;
    private final AutenticacionService autenticacionService;

    public AuthController(RegistroService registroService, AutenticacionService autenticacionService) {
        this.registroService = registroService;
        this.autenticacionService = autenticacionService;
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
    public SesionResponse login(@RequestBody com.agrovalle.connect.dto.LoginRequest solicitud,
            HttpServletRequest request) {
        AutenticacionService.UsuarioAutenticado usuario = autenticacionService.autenticar(solicitud);
        HttpSession previous = request.getSession(false);
        if (previous != null) {
            previous.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("usuarioId", usuario.id());
        session.setAttribute("tipoUsuario", usuario.tipo());
        session.setAttribute("nombreUsuario", usuario.nombre());
        return new SesionResponse(true, usuario.tipo(), usuario.nombre());
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
        return autenticacionService.obtenerPerfil(
                (String) session.getAttribute("tipoUsuario"), (Long) session.getAttribute("usuarioId"));
    }

    @PostMapping("/profile")
    public PerfilResponse actualizarPerfil(@RequestBody PerfilActualizacionRequest datos,
            HttpServletRequest request) {
        HttpSession session = exigirSesion(request);
        String tipo = (String) session.getAttribute("tipoUsuario");
        Long id = (Long) session.getAttribute("usuarioId");
        PerfilResponse perfil = autenticacionService.actualizarPerfil(tipo, id, datos);
        session.setAttribute("nombreUsuario", nombreCompleto(perfil.nombre(), perfil.apellido()));
        return perfil;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    private HttpSession exigirSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("tipoUsuario") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inicia sesión para continuar.");
        }
        return session;
    }

    private String nombreCompleto(String nombre, String apellido) {
        String resultado = ((nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido)).trim();
        return resultado.isBlank() ? nombre : resultado;
    }
}
