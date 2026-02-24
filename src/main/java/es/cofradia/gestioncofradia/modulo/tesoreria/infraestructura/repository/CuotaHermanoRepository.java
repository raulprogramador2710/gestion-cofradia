package es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.CuotaHermano;

public interface CuotaHermanoRepository extends JpaRepository<CuotaHermano, Long> {

    List<CuotaHermano> findByHermanoId(Long hermanoId);

    List<CuotaHermano> findByCuotaId(Long cuotaId);

    boolean existsByCuotaIdAndHermanoId(Long cuotaId, Long hermanoId);
}