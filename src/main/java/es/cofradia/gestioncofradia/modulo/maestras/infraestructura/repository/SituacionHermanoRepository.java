package es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.cofradia.gestioncofradia.modulo.maestras.dominio.SituacionHermano;

@Repository
public interface SituacionHermanoRepository extends JpaRepository<SituacionHermano, Long> {
    
	Optional<SituacionHermano> findByCodigo(String codigo);
    
}
