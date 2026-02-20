package es.cofradia.gestioncofradia.repository.maestras;

import es.cofradia.gestioncofradia.model.maestras.EstadoHermano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstadoHermanoRepository extends JpaRepository<EstadoHermano, Long> {
    
	Optional<EstadoHermano> findByCodigo(String codigo);
    
}
