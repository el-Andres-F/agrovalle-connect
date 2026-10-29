package com.agrovalle.connect.controlador;

import com.agrovalle.connect.repositorio.PedidoRepository;
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
    private final PedidoRepository pedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/mios")
    public List<PedidoResponse> misPedidos(HttpSession session) {
        if (session == null || !"COMPRADOR".equals(session.getAttribute("tipoUsuario"))) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Inicia sesión como comprador para consultar tus pedidos.");
        }
        Long compradorId = (Long) session.getAttribute("usuarioId");
        return pedidoRepository.findByCompradorIdOrderByFechaCreacionDesc(compradorId).stream()
                .map(p -> new PedidoResponse(p.getReferencia(), p.getTotal(), p.getEstado(),
                        p.getFechaCreacion().toString(), p.getDetalle()))
                .toList();
    }

    public record PedidoResponse(String referencia, java.math.BigDecimal total, String estado,
            String fechaCreacion, String detalle) { }
}
