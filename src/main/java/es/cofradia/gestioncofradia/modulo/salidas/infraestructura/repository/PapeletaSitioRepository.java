package es.cofradia.gestioncofradia.modulo.salidas.infraestructura.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.cofradia.gestioncofradia.modulo.salidas.dominio.PapeletaSitio;

@Repository
public interface PapeletaSitioRepository extends JpaRepository<PapeletaSitio, Long> {

    // Todas las papeletas de un hermano (histórico completo)
    List<PapeletaSitio> findByHermanoIdOrderByAnioDesc(Long hermanoId);

    // Papeleta de un hermano en un año concreto
    Optional<PapeletaSitio> findByHermanoIdAndAnio(Long hermanoId, Integer anio);

    // Todas las papeletas de una cofradía en un año concreto
    @Query("SELECT p FROM PapeletaSitio p WHERE p.hermano.cofradia.id = :cofradiaId AND p.anio = :anio ORDER BY p.hermano.numHermano ASC")
    List<PapeletaSitio> findByCofradiaIdAndAnio(@Param("cofradiaId") Long cofradiaId, @Param("anio") Integer anio);

    // Papeletas de una cofradía en un año filtradas por tipo de participación
    @Query("SELECT p FROM PapeletaSitio p WHERE p.hermano.cofradia.id = :cofradiaId AND p.anio = :anio AND p.tipoParticipacion.id = :tipoId ORDER BY p.hermano.numHermano ASC")
    List<PapeletaSitio> findByCofradiaIdAndAnioAndTipo(@Param("cofradiaId") Long cofradiaId, @Param("anio") Integer anio, @Param("tipoId") Long tipoId);

    // Papeletas pendientes de pago de una cofradía en un año
    @Query("SELECT p FROM PapeletaSitio p WHERE p.hermano.cofradia.id = :cofradiaId AND p.anio = :anio AND p.pagada = false")
    List<PapeletaSitio> findPendientesPagoByCofradiaIdAndAnio(@Param("cofradiaId") Long cofradiaId, @Param("anio") Integer anio);
}