package com.agrovalle.connect.repositorio;

import com.agrovalle.connect.modelo.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCompradorIdOrderByFechaCreacionDesc(Long compradorId);

    List<Pedido> findAllByOrderByFechaCreacionDesc();
}
