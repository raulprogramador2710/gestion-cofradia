package es.cofradia.gestioncofradia.modulo.tesoreria.aplicacion;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.cofradia.gestioncofradia.modulo.hermanos.infraestructura.repository.HermanoRepository;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.SituacionPagoHermano;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.SituacionPagoHermanoRepository;
import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.Cuota;
import es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.repository.CuotaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CuotaService {

    private final CuotaRepository cuotaRepo;
    private final HermanoRepository hermanoRepo;
    private final SituacionPagoHermanoRepository situacionPagoRepo;

    @Transactional
    public void guardarYActivar(Cuota cuota) {
        // 1. Si esta cuota es activa, desactivamos las anteriores del mismo tipo
        if (cuota.isActiva()) {
            List<Cuota> anteriores = cuotaRepo.findByCofradiaId(cuota.getCofradia().getId());
            anteriores.forEach(c -> {
                if (c.getTipo().getId().equals(cuota.getTipo().getId())) {
                    c.setActiva(false);
                }
            });
            cuotaRepo.saveAll(anteriores);
        }

        // 2. Guardamos la nueva cuota
        cuotaRepo.save(cuota);

        // 3. REGLA DE NEGOCIO: Si es Cuota Anual Activa → todos los hermanos pasan a DEUDOR
        if (cuota.isActiva() && "ANUAL".equals(cuota.getTipo().getCodigo())) {
            SituacionPagoHermano deudor = situacionPagoRepo.findByCodigo("DEUDOR")
                    .orElseThrow(() -> new RuntimeException("No existe situación DEUDOR en maestras"));
            hermanoRepo.actualizarSituacionPagoMasivo(cuota.getCofradia().getId(), deudor.getId());
        }
    }

    public List<Cuota> listarPorCofradia(Long cofradiaId) {
        return cuotaRepo.findByCofradiaId(cofradiaId);
    }

    public Cuota buscarPorId(Long id) {
        return cuotaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Cuota no encontrada"));
    }
}