package es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cofradia.gestioncofradia.modulo.maestras.dominio.TipoCuota;

public interface TipoCuotaRepository extends JpaRepository<TipoCuota, Long> {
    Optional<TipoCuota> findByCodigo(String codigo);
}
