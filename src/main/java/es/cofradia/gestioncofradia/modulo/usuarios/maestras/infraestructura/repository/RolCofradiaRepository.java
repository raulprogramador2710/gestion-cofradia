package es.cofradia.gestioncofradia.modulo.usuarios.maestras.infraestructura.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cofradia.gestioncofradia.modulo.usuarios.maestras.dominio.RolCofradia;

public interface RolCofradiaRepository extends JpaRepository<RolCofradia, Long> {

	Optional<RolCofradia> findByCodigo(String codigo);
	
}