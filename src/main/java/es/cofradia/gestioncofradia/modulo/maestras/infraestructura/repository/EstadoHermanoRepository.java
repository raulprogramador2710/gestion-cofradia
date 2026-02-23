package es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.cofradia.gestioncofradia.modulo.maestras.dominio.EstadoHermano;

import java.util.Optional;

@Repository
public interface EstadoHermanoRepository extends JpaRepository<EstadoHermano, Long> {
    
	Optional<EstadoHermano> findByCodigo(String codigo);
    
}
