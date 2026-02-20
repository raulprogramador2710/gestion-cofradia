package es.cofradia.gestioncofradia.repository.maestras;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.cofradia.gestioncofradia.model.maestras.FormaComunicacion;

@Repository
public interface FormaComunicacionRepository extends JpaRepository<FormaComunicacion, Long> {
   
	Optional<FormaComunicacion> findByCodigo(String codigo);
    
}
