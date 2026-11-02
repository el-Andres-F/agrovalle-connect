package com.agrovalle.connect.servicio;

import com.agrovalle.connect.dto.PedidoResponse;
import com.agrovalle.connect.repositorio.PedidoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read-only order history use case for an authenticated buyer. */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorComprador(Long compradorId) {
        return pedidoRepository.findByCompradorIdOrderByFechaCreacionDesc(compradorId).stream()
                .map(pedido -> new PedidoResponse(pedido.getReferencia(), pedido.getTotal(), pedido.getEstado(),
                        pedido.getFechaCreacion().toString(), pedido.getDetalle()))
                .toList();
    }
}
