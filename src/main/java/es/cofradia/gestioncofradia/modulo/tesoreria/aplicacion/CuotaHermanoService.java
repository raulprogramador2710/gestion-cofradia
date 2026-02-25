package es.cofradia.gestioncofradia.modulo.tesoreria.aplicacion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import jakarta.persistence.EntityNotFoundException;
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
    	
        Hermano hermano = hermanoRepo.findById(hermanoId).orElseThrow(() -> new IllegalArgumentException("Hermano no encontrado"));

        // 1. Buscamos la cuota anual activa de la cofradía
        Cuota cuotaActiva = cuotaRepo.findFirstByCofradiaIdAndTipoCodigoAndActivaTrueOrderByAnioDesc(cofradiaId, "ANUAL").
        		orElseThrow(() -> new RuntimeException("No hay cuota anual activa configurada"));

        // 2. Evitar duplicados: si ya existe el registro, no hacemos nada
        if (cuotaHermanoRepo.existsByCuotaIdAndHermanoId(cuotaActiva.getId(), hermanoId)) {
            throw new RuntimeException("Este hermano ya tiene registrado el pago de esta cuota");
        }

        // 3. Creamos el registro de pago
        SituacionPagoHermano alDia = situacionPagoRepo.findByCodigo("PAGADO").orElseThrow(() -> new RuntimeException("No existe situación PAGADO en maestras"));

        CuotaHermano pago = CuotaHermano.builder()
                .cuota(cuotaActiva)
                .hermano(hermano)
                .importeFinal(cuotaActiva.getImporte())
                .situacionPago(alDia)
                .fechaPago(LocalDate.now())
                .generadoPor("SECRETARIO")
                .build();

        cuotaHermanoRepo.save(pago);

        // 4. Actualizamos solo el año del último pago en la ficha del hermano
        hermano.setFechaUltimoPago(cuotaActiva.getAnio());
        hermanoRepo.save(hermano);
    }
    
    public List<CuotaHermano> buscarPorHermano(Hermano hermano) {
        
    	return cuotaHermanoRepo.findByHermanoOrderByCuotaAnioDesc(hermano);
    	
    }
    
    @Transactional
    public void marcarComoPagada(Long id) {
    	
        CuotaHermano cuota = cuotaHermanoRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Cuota no encontrada"));
        
        SituacionPagoHermano pagada = situacionPagoRepo.findByCodigo("PAGADO").orElseThrow(() -> new EntityNotFoundException("Situación de pago 'PAGADO' no encontrada"));
        
        cuota.setSituacionPago(pagada);
        cuota.setFechaPago(LocalDate.now());
        
        cuotaHermanoRepo.save(cuota);
    }
    
    @Transactional
    public void crearCuotaHermanoSiCuotaActiva(Hermano hermano) {
        if (hermano == null || hermano.getCofradia() == null) return;

        // 1) obtener cuota activa más reciente para la cofradía
        Optional<Cuota> optCuota = cuotaRepo.findFirstByCofradiaIdAndActivaTrueOrderByAnioDesc(hermano.getCofradia().getId());
        if (optCuota.isEmpty()) return;

        Cuota cuotaActiva = optCuota.get();

        // 2) comprobar duplicado
        if (cuotaHermanoRepo.existsByCuotaIdAndHermanoId(cuotaActiva.getId(), hermano.getId())) {
            return; // ya existe → no crear
        }

        // 3) obtener situación "PENDIENTE" (o fallback)
        SituacionPagoHermano pendiente = situacionPagoRepo.findByCodigo("PENDIENTE")
                .orElseGet(() -> situacionPagoRepo.findAll().stream().findFirst().orElse(null));

        // 4) crear y guardar
        CuotaHermano ch = new CuotaHermano();
        ch.setCuota(cuotaActiva);
        ch.setHermano(hermano);
        ch.setCofradia(hermano.getCofradia());
        ch.setSituacionPago(pendiente);
        // ajustar importe según la entidad: uso importeBase como ejemplo
        ch.setImporteFinal(cuotaActiva.getImporte());

        cuotaHermanoRepo.save(ch);
    }
    
}