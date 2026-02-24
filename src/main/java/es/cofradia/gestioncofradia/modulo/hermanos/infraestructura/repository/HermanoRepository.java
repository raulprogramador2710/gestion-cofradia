package es.cofradia.gestioncofradia.modulo.hermanos.infraestructura.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.cofradia.gestioncofradia.modulo.cofradias.dominio.Cofradia;
import es.cofradia.gestioncofradia.modulo.hermanos.aplicacion.dto.HermanoListaDTO;
import es.cofradia.gestioncofradia.modulo.hermanos.dominio.Hermano;

@Repository
public interface HermanoRepository extends JpaRepository<Hermano, Long> {

    List<Hermano> findByCofradia(Cofradia cofradia);

    List<Hermano> findByDni(String dni);

    long countByCofradia(Cofradia cofradia);

    List<Hermano> findByCofradiaAndApellidosContainingIgnoreCase(Cofradia cofradia, String apellidos);

    Optional<Hermano> findByCofradiaAndNumHermano(Cofradia cofradia, Integer numHermano);

    @Query("select max(h.numHermano) from Hermano h where h.cofradia = :cofradia")
    Integer findMaxNumByCofradia(@Param("cofradia") Cofradia cofradia);

    boolean existsByDniAndCofradiaId(String dni, Long cofradiaId);

    Optional<Hermano> findByDniAndCofradiaId(String dni, Long cofradiaId);

    // ✅ Eliminado "situacionPago" del EntityGraph
    @EntityGraph(attributePaths = {"situacion", "formaPago", "formaComunicacion"})
    List<Hermano> findByCofradiaIdOrderByNumHermanoAsc(Long cofradiaId);

    @Query("SELECT COALESCE(MAX(h.numHermano), 0) FROM Hermano h WHERE h.cofradia.id = :cofradiaId")
    Integer findMaxNumHermanoByCofradiaId(@Param("cofradiaId") Long cofradiaId);

    // ✅ Eliminado "situacionPago" del EntityGraph
    @EntityGraph(attributePaths = {"situacion", "formaPago", "formaComunicacion"})
    @Query("""
        SELECT h FROM Hermano h 
        WHERE h.cofradia.id = :cofradiaId
          AND (:filtroNombre IS NULL OR LOWER(h.nombre) LIKE LOWER(CONCAT('%', CAST(:filtroNombre AS string), '%'))
               OR LOWER(h.apellidos) LIKE LOWER(CONCAT('%', CAST(:filtroNombre AS string), '%')))
          AND (:filtroDni IS NULL OR LOWER(h.dni) LIKE LOWER(CONCAT('%', CAST(:filtroDni AS string), '%')))
        """)
    Page<Hermano> buscarPorCofradiaConFiltros(
        @Param("cofradiaId") Long cofradiaId,
        @Param("filtroNombre") String filtroNombre,
        @Param("filtroDni") String filtroDni,
        Pageable pageable
    );

    List<Hermano> findByCofradiaIdAndSituacionCodigo(Long cofradiaId, String codigo);

    List<Hermano> findByCofradiaIdAndSituacionCodigoIn(Long cofradiaId, List<String> codigos);
    
    @Query("""
    	    SELECT new es.cofradia.gestioncofradia.modulo.hermanos.aplicacion.dto.HermanoListaDTO(
    	        h.id, h.numHermano, h.nombre, h.apellidos, h.dni,
    	        s.codigo, s.codigoVisual,
    	        sp.codigo, sp.codigoVisual,
    	        tp.nombre
    	    )
    	    FROM Hermano h
    	    LEFT JOIN h.situacion s
    	    LEFT JOIN h.tipoParticipacion tp
    	    LEFT JOIN CuotaHermano ch ON ch.hermano = h
    		    AND ch.cofradia.id = :cofradiaId
    	        AND ch.cuota.anio = (
    	            SELECT MAX(c2.anio) FROM Cuota c2 WHERE c2.cofradia.id = :cofradiaId
    	        )
    	    LEFT JOIN ch.situacionPago sp
    	    WHERE h.cofradia.id = :cofradiaId
    	      AND (:filtroNombre IS NULL OR LOWER(h.nombre) LIKE LOWER(CONCAT('%', CAST(:filtroNombre AS string), '%'))
    	           OR LOWER(h.apellidos) LIKE LOWER(CONCAT('%', CAST(:filtroNombre AS string), '%')))
    	      AND (:filtroDni IS NULL OR LOWER(h.dni) LIKE LOWER(CONCAT('%', CAST(:filtroDni AS string), '%')))
    	""")
	Page<HermanoListaDTO> buscarHermanosConEstadoUltimaCuota(
	    @Param("cofradiaId") Long cofradiaId,
	    @Param("filtroNombre") String filtroNombre,
	    @Param("filtroDni") String filtroDni,
	    Pageable pageable
	);
}