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
import es.cofradia.gestioncofradia.modulo.hermanos.dominio.Hermano;

@Repository
public interface HermanoRepository extends JpaRepository<Hermano, Long> {

	List<Hermano> findByCofradia(Cofradia cofradia);

	List<Hermano> findByDni(String dni);

    long countByCofradia(Cofradia cofradia);

    List<Hermano> findByCofradiaAndApellidosContainingIgnoreCase(Cofradia cofradia, String apellidos);

    // Buscar por número de hermano dentro de una cofradía
    Optional<Hermano> findByCofradiaAndNumHermano(Cofradia cofradia, Integer numHermano);

    // Obtener máximo numHermano para una cofradía (null si no hay ninguno)
    @Query("select max(h.numHermano) from Hermano h where h.cofradia = :cofradia")
    Integer findMaxNumByCofradia(@Param("cofradia") Cofradia cofradia);
    
    boolean existsByDniAndCofradiaId(String dni, Long cofradiaId);
    
    Optional<Hermano> findByDniAndCofradiaId(String dni, Long cofradiaId);
    
    // Trae los hermanos de una cofradía ordenados por numHermano.
    @EntityGraph(attributePaths = {"estado"}) // opcional pero evita problemas LZY al mostrar estado
    List<Hermano> findByCofradiaIdOrderByNumHermanoAsc(Long cofradiaId);
    
    @Query("SELECT COALESCE(MAX(h.numHermano), 0) FROM Hermano h WHERE h.cofradia.id = :cofradiaId")
    Integer findMaxNumHermanoByCofradiaId(@Param("cofradiaId") Long cofradiaId);
    
    @EntityGraph(attributePaths = {"estado"})
    @Query("SELECT h FROM Hermano h WHERE h.cofradia.id = :cofradiaId "
    	     + "AND (:filtroNombre IS NULL OR LOWER(h.nombre) LIKE LOWER(CAST(CONCAT('%', :filtroNombre, '%') AS string)) "
    	     + "OR LOWER(h.apellidos) LIKE LOWER(CAST(CONCAT('%', :filtroNombre, '%') AS string))) "
    	     + "AND (:filtroDni IS NULL OR LOWER(h.dni) LIKE LOWER(CAST(CONCAT('%', :filtroDni, '%') AS string)))")
    	Page<Hermano> buscarPorCofradiaConFiltros(
    	    @Param("cofradiaId") Long cofradiaId, 
    	    @Param("filtroNombre") String filtroNombre, 
    	    @Param("filtroDni") String filtroDni, 
    	    Pageable pageable);
    
}