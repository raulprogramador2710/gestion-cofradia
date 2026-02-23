package es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.cofradia.gestioncofradia.modulo.maestras.dominio.FormaComunicacion;

@Repository
public interface FormaComunicacionRepository extends JpaRepository<FormaComunicacion, Long> {
   
	Optional<FormaComunicacion> findByCodigo(String codigo);
    
}
