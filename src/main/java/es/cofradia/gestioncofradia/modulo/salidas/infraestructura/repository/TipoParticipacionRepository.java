package es.cofradia.gestioncofradia.modulo.salidas.infraestructura.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.cofradia.gestioncofradia.modulo.salidas.dominio.TipoParticipacion;

@Repository
public interface TipoParticipacionRepository extends JpaRepository<TipoParticipacion, Long> {

    boolean existsByNombre(String nombre);
}