package com.agrovalle.connect.repositorio;

import com.agrovalle.connect.modelo.Comprador;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompradorRepository extends JpaRepository<Comprador, Long> {

    boolean existsByCorreo(String correo);

    Optional<Comprador> findByCorreo(String correo);

    boolean existsByCorreoAndIdNot(String correo, Long id);
}
