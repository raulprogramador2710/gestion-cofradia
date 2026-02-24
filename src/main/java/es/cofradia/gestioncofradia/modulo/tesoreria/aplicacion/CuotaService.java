package es.cofradia.gestioncofradia.modulo.tesoreria.aplicacion;

import es.cofradia.gestioncofradia.modulo.cofradias.dominio.Cofradia;
import es.cofradia.gestioncofradia.modulo.cofradias.infraestructura.repository.CofradiaRepository;
import es.cofradia.gestioncofradia.modulo.hermanos.dominio.Hermano;
import es.cofradia.gestioncofradia.modulo.hermanos.infraestructura.repository.HermanoRepository;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.SituacionPagoHermano;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.TipoCuota;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.SituacionPagoHermanoRepository;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.TipoCuotaRepository;
import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.Cuota;
import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.CuotaHermano;
import es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.repository.CuotaHermanoRepository;
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
    private final HermanoRepository hermanoRepository;
    private final CuotaHermanoRepository cuotaHermanoRepository;
    private final SituacionPagoHermanoRepository situacionPagoHermanoRepository;

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
        Cuota cuotaGuardada = cuotaRepository.save(cuota);

        // Solo al crear (id nuevo), generamos los CuotaHermano automáticamente
        if (cuota.getId() == null) {
            generarCuotasHermanos(cuotaGuardada, cofradia);
        }
    }

    private void generarCuotasHermanos(Cuota cuota, Cofradia cofradia) {
        SituacionPagoHermano noPagado = situacionPagoHermanoRepository.findByCodigo("DEUDOR").orElseThrow(() -> new RuntimeException("SituacionPago 'DEUDOR' no encontrada en maestras"));

        // Hermanos ACTIVO y FALLECIDO → se les genera cuota
        List<Hermano> hermanos = hermanoRepository.findByCofradiaIdAndSituacionCodigoIn(cofradia.getId(), List.of("ACTIVO", "FALLECIDO"));

        List<CuotaHermano> cuotasHermanos = hermanos.stream()
                .map(hermano -> CuotaHermano.builder()
                        .cuota(cuota)
                        .hermano(hermano)
                        .importeFinal(cuota.getImporteBase())
                        .situacionPago(noPagado)
                        .generadoPor("SECRETARIO")
                        .build())
                .toList();

        cuotaHermanoRepository.saveAll(cuotasHermanos);
    }

    @Transactional
    public void eliminar(Long id) {
        cuotaRepository.deleteById(id);
    }
}