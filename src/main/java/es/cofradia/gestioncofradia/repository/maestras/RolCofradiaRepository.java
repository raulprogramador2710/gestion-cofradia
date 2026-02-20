package es.cofradia.gestioncofradia.repository.maestras;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cofradia.gestioncofradia.model.maestras.RolCofradia;

public interface RolCofradiaRepository extends JpaRepository<RolCofradia, Long> {

	Optional<RolCofradia> findByCodigo(String codigo);
	
}