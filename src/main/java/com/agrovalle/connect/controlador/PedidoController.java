package com.agrovalle.connect.controlador;

import com.agrovalle.connect.dto.PedidoResponse;
import com.agrovalle.connect.servicio.PedidoService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/mios")
    public List<PedidoResponse> misPedidos(HttpSession session) {
        if (session == null || !"COMPRADOR".equals(session.getAttribute("tipoUsuario"))) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Inicia sesión como comprador para consultar tus pedidos.");
        }
        Long compradorId = (Long) session.getAttribute("usuarioId");
        return pedidoService.listarPorComprador(compradorId);
    }
}
