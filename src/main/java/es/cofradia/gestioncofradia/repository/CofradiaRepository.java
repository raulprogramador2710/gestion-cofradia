package es.cofradia.gestioncofradia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.cofradia.gestioncofradia.model.Cofradia;

@Repository
public interface CofradiaRepository extends JpaRepository<Cofradia, Long> {
	
	Optional<Cofradia> findByNombre(String nombre);
	
}