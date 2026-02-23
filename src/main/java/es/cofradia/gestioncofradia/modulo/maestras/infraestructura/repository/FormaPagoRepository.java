package es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.cofradia.gestioncofradia.modulo.maestras.dominio.FormaPago;

import java.util.Optional;

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, Long> {
    
	Optional<FormaPago> findByCodigo(String codigo);
    
}
