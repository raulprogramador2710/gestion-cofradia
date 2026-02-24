package es.cofradia.gestioncofradia.modulo.tesoreria.aplicacion;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.cofradia.gestioncofradia.modulo.hermanos.dominio.Hermano;
import es.cofradia.gestioncofradia.modulo.hermanos.infraestructura.repository.HermanoRepository;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.SituacionPagoHermano;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.SituacionPagoHermanoRepository;
import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.Cuota;
import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.CuotaHermano;
import es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.repository.CuotaHermanoRepository;
import es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.repository.CuotaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CuotaHermanoService {

    private final CuotaHermanoRepository cuotaHermanoRepo;
    private final HermanoRepository hermanoRepo;
    private final CuotaRepository cuotaRepo;
    private final SituacionPagoHermanoRepository situacionPagoRepo;

    @Transactional
    public void registrarPagoAnual(Long hermanoId, Long cofradiaId) {
        Hermano hermano = hermanoRepo.findById(hermanoId)
                .orElseThrow(() -> new IllegalArgumentException("Hermano no encontrado"));

        // 1. Buscamos la cuota anual activa de la cofradía
        Cuota cuotaActiva = cuotaRepo
                .findFirstByCofradiaIdAndTipoCodigoAndActivaTrueOrderByAnioDesc(cofradiaId, "ANUAL")
                .orElseThrow(() -> new RuntimeException("No hay cuota anual activa configurada"));

        // 2. Evitar duplicados: si ya pagó esta cuota, no hacemos nada
        if (cuotaHermanoRepo.existsByCuotaIdAndHermanoId(cuotaActiva.getId(), hermanoId)) {
            throw new RuntimeException("Este hermano ya tiene registrado el pago de esta cuota");
        }

        // 3. Creamos el registro de pago en la tabla intermedia
        SituacionPagoHermano alDia = situacionPagoRepo.findByCodigo("AL_DIA")
                .orElseThrow(() -> new RuntimeException("No existe situación AL_DIA en maestras"));

        CuotaHermano pago = CuotaHermano.builder()
                .cuota(cuotaActiva)
                .hermano(hermano)
                .importeFinal(cuotaActiva.getImporteBase())
                .situacionPago(alDia)
                .fechaPago(LocalDate.now())
                .generadoPor("SECRETARIO")
                .build();

        cuotaHermanoRepo.save(pago);

        // 4. Actualizamos la ficha del hermano
        hermano.setSituacionPago(alDia);
        hermano.setFechaUltimoPago(cuotaActiva.getAnio());
        hermanoRepo.save(hermano);
    }
}