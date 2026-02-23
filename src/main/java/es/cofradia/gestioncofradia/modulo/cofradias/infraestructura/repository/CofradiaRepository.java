package es.cofradia.gestioncofradia.modulo.cofradias.infraestructura.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.cofradia.gestioncofradia.modulo.cofradias.dominio.Cofradia;

@Repository
public interface CofradiaRepository extends JpaRepository<Cofradia, Long> {
	
	Optional<Cofradia> findByNombre(String nombre);
	
}