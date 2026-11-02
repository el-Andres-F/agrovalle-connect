package com.agrovalle.connect.controlador;

import com.agrovalle.connect.dto.PagoPseResponse;
import com.agrovalle.connect.dto.SolicitudPagoPse;
import com.agrovalle.connect.servicio.PagoPseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PagoPseController {

    private final PagoPseService pagoPseService;

    public PagoPseController(PagoPseService pagoPseService) {
        this.pagoPseService = pagoPseService;
    }

    @PostMapping("/api/v1/pagos/pse")
    public PagoPseResponse iniciarPago(@RequestBody SolicitudPagoPse solicitud, HttpSession session) {
        if (session == null || !"COMPRADOR".equals(session.getAttribute("tipoUsuario"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inicia sesión como cliente para pagar.");
        }
        return pagoPseService.iniciar(solicitud, (Long) session.getAttribute("usuarioId"));
    }
}
