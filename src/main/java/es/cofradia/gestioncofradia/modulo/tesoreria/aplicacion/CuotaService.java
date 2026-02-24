package es.cofradia.gestioncofradia.modulo.tesoreria.aplicacion;

import es.cofradia.gestioncofradia.modulo.cofradias.dominio.Cofradia;
import es.cofradia.gestioncofradia.modulo.cofradias.infraestructura.repository.CofradiaRepository;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.TipoCuota;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.TipoCuotaRepository;
import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.Cuota;
import es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.repository.CuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuotaService {

    private final CuotaRepository cuotaRepository;
    private final CofradiaRepository cofradiaRepository;
    private final TipoCuotaRepository tipoCuotaRepository;

    public List<Cuota> findByCofradiaId(Long cofradiaId) {
        return cuotaRepository.findByCofradiaId(cofradiaId);
    }

    public Cuota findById(Long id) {
        return cuotaRepository.findById(id).orElseThrow(() -> new RuntimeException("Cuota no encontrada"));
    }

    public List<TipoCuota> findAllTiposCuota() {
        return tipoCuotaRepository.findAll();
    }

    @Transactional
    public void guardar(Cuota cuota, Long cofradiaId) {
        Cofradia cofradia = cofradiaRepository.findById(cofradiaId).orElseThrow();
        cuota.setCofradia(cofradia);
        cuotaRepository.save(cuota);
    }

    @Transactional
    public void eliminar(Long id) {
        cuotaRepository.deleteById(id);
    }
}