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
    public void guardar(Cuota cuota, Long cofradiaId, String generadoPor) {
    	Cofradia cofradia = cofradiaRepository.findById(cofradiaId).orElseThrow(() -> new RuntimeException("Cofradía no encontrada"));
            
        cuota.setCofradia(cofradia);
        
        // Guardamos la cuota primero
        Cuota cuotaGuardada = cuotaRepository.save(cuota);

        // Generamos los registros para los hermanos siempre que sea una creación nueva
        // Usamos la cuota recién guardada que ya tiene ID
        generarCuotasHermanos(cuotaGuardada, cofradia, generadoPor);
    }

    private void generarCuotasHermanos(Cuota cuota, Cofradia cofradia, String generadoPor) {
    	// 1. Buscar situación PENDIENTE (asegúrate que el código existe en la DB)
        SituacionPagoHermano noPagado = situacionPagoHermanoRepository.findByCodigo("PENDIENTE").orElseThrow(() -> new RuntimeException("SituacionPago 'PENDIENTE' no encontrada"));

        // 2. Obtener hermanos que deben recibir la cuota
        List<Hermano> hermanos = hermanoRepository.findByCofradiaIdAndSituacionCodigoIn(cofradia.getId(), List.of("ACTIVO", "FALLECIDO"));

        // 3. Mapear y guardar
        List<CuotaHermano> cuotasHermanos = hermanos.stream()
                .map(hermano -> CuotaHermano.builder()
                        .cuota(cuota)
                        .hermano(hermano)
                        .cofradia(cofradia)
                        .importeFinal(cuota.getImporte())
                        .situacionPago(noPagado)
                        .generadoPor(generadoPor)
                        .build())
                .toList();

        if (!cuotasHermanos.isEmpty()) {
            cuotaHermanoRepository.saveAll(cuotasHermanos);
        }
    }

    @Transactional
    public void eliminar(Long id) {
        cuotaRepository.deleteById(id);
    }
    
    @Transactional
    public void marcarComoPagada(Long cuotaHermanoId) {
        CuotaHermano ch = cuotaHermanoRepository.findById(cuotaHermanoId).orElseThrow(() -> new RuntimeException("Registro de cuota no encontrado"));

        SituacionPagoHermano pagado = situacionPagoHermanoRepository.findByCodigo("PAGADO").orElseThrow(() -> new RuntimeException("Situación 'PAGADO' no encontrada"));

        ch.setSituacionPago(pagado);
        ch.setFechaPago(java.time.LocalDate.now());
        ch.setImporteFinal(ch.getImporteFinal());
        
        cuotaHermanoRepository.save(ch);
    }
    
}