package com.agrovalle.connect.repositorio;

import com.agrovalle.connect.modelo.Productor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductorRepository extends JpaRepository<Productor, Long> {
}
