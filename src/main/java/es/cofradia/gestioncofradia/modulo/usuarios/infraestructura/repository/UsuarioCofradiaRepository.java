package es.cofradia.gestioncofradia.modulo.usuarios.infraestructura.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cofradia.gestioncofradia.modulo.cofradias.dominio.Cofradia;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.Usuario;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.UsuarioCofradia;

public interface UsuarioCofradiaRepository extends JpaRepository<UsuarioCofradia, Long> {

	List<UsuarioCofradia> findByUsuario(Usuario usuario);
    Optional<UsuarioCofradia> findByUsuarioAndCofradia(Usuario usuario, Cofradia cofradia);

    List<UsuarioCofradia> findByCofradia(Cofradia cofradia);
	
}