package com.agrovalle.connect.servicio;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AutorizacionService {

    public void exigirAdministrador(HttpSession session) {
        if (session == null || !"ADMIN".equals(session.getAttribute("tipoUsuario"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Se requiere una sesión de administrador.");
        }
    }
}
