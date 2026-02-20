package es.cofradia.gestioncofradia.repository;

import es.cofradia.gestioncofradia.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByUsuario(String username);
    boolean existsByUsuario(String username);
	
}