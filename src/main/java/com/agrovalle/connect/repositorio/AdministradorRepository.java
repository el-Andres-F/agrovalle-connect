package com.agrovalle.connect.repositorio;

import com.agrovalle.connect.modelo.Administrador;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    Optional<Administrador> findByUsuario(String usuario);

    boolean existsByUsuario(String usuario);

    boolean existsByCorreoAndIdNot(String correo, Long id);
}
