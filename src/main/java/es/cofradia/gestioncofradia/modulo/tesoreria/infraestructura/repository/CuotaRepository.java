package es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.Cuota;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {

    List<Cuota> findByCofradiaId(Long cofradiaId);

    Optional<Cuota> findFirstByCofradiaIdAndTipoCodigoAndActivaTrueOrderByAnioDesc(Long cofradiaId, String tipoCodigo);
}