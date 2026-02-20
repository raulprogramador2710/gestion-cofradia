package es.cofradia.gestioncofradia.repository.maestras;

import es.cofradia.gestioncofradia.model.maestras.FormaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, Long> {
    
	Optional<FormaPago> findByCodigo(String codigo);
    
}
