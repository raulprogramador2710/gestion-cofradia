package es.cofradia.gestioncofradia.modulo.usuarios.infraestructura.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.cofradia.gestioncofradia.modulo.usuarios.dominio.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByUsuario(String username);
    boolean existsByUsuario(String username);
	
}