package es.cofradia.gestioncofradia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cofradia.gestioncofradia.model.Cofradia;
import es.cofradia.gestioncofradia.model.Usuario;
import es.cofradia.gestioncofradia.model.UsuarioCofradia;

public interface UsuarioCofradiaRepository extends JpaRepository<UsuarioCofradia, Long> {

	List<UsuarioCofradia> findByUsuario(Usuario usuario);
    Optional<UsuarioCofradia> findByUsuarioAndCofradia(Usuario usuario, Cofradia cofradia);

    List<UsuarioCofradia> findByCofradia(Cofradia cofradia);
	
}