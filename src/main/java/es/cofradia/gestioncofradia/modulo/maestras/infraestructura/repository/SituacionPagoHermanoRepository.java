package es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cofradia.gestioncofradia.modulo.maestras.dominio.SituacionPagoHermano;

public interface SituacionPagoHermanoRepository extends JpaRepository<SituacionPagoHermano, Long> {
	
	Optional<SituacionPagoHermano> findByCodigo(String codigo);
	
}