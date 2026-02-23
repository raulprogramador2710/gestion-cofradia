package es.cofradia.gestioncofradia.modulo.salidas.aplicacion;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import es.cofradia.gestioncofradia.modulo.salidas.dominio.PapeletaSitio;
import es.cofradia.gestioncofradia.modulo.salidas.dominio.TipoParticipacion;
import es.cofradia.gestioncofradia.modulo.salidas.infraestructura.repository.PapeletaSitioRepository;
import es.cofradia.gestioncofradia.modulo.salidas.infraestructura.repository.TipoParticipacionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalidaService {

    private final PapeletaSitioRepository papeletaRepo;
    private final TipoParticipacionRepository tipoParticipacionRepo;

    // --- TipoParticipacion ---

    public List<TipoParticipacion> listarTiposParticipacion() {
        return tipoParticipacionRepo.findAll();
    }

    public void guardarTipoParticipacion(TipoParticipacion tipo) {
        tipoParticipacionRepo.save(tipo);
    }

    public void eliminarTipoParticipacion(Long id) {
        tipoParticipacionRepo.deleteById(id);
    }

    // --- PapeletaSitio ---

    public List<PapeletaSitio> listarPorCofradiaYAnio(Long cofradiaId, Integer anio) {
        return papeletaRepo.findByCofradiaIdAndAnio(cofradiaId, anio);
    }

    public List<PapeletaSitio> listarPorCofradiaAnioYTipo(Long cofradiaId, Integer anio, Long tipoId) {
        return papeletaRepo.findByCofradiaIdAndAnioAndTipo(cofradiaId, anio, tipoId);
    }

    public List<PapeletaSitio> listarHistoricoHermano(Long hermanoId) {
        return papeletaRepo.findByHermanoIdOrderByAnioDesc(hermanoId);
    }

    public List<PapeletaSitio> listarPendientesPago(Long cofradiaId, Integer anio) {
        return papeletaRepo.findPendientesPagoByCofradiaIdAndAnio(cofradiaId, anio);
    }

    public void emitirPapeleta(PapeletaSitio papeleta) {
        papeleta.setFechaEmision(LocalDateTime.now());
        papeletaRepo.save(papeleta);
    }

    public void registrarPago(Long papeletaId, Double importe) {
        PapeletaSitio papeleta = papeletaRepo.findById(papeletaId)
                .orElseThrow(() -> new IllegalArgumentException("Papeleta no encontrada"));
        papeleta.setPagada(true);
        papeleta.setImportePagado(importe);
        papeletaRepo.save(papeleta);
    }

    public void eliminarPapeleta(Long id) {
        papeletaRepo.deleteById(id);
    }
}