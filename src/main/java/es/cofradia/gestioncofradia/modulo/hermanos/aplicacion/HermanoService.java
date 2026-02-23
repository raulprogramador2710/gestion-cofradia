package es.cofradia.gestioncofradia.modulo.hermanos.aplicacion;

import java.util.List;

import org.springframework.stereotype.Service;

import es.cofradia.gestioncofradia.modulo.hermanos.dominio.Hermano;
import es.cofradia.gestioncofradia.modulo.hermanos.infraestructura.repository.HermanoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HermanoService {
	
	private final HermanoRepository hermanoRepo;

	public void eliminarPorId(Long id) {
        hermanoRepo.deleteById(id);
    }

    public List<Hermano> listarPorCofradia(Long cofradiaId) {
        return hermanoRepo.findByCofradiaIdOrderByNumHermanoAsc(cofradiaId);
    }
	
	public void guardar(Hermano hermano) {
	    hermanoRepo.save(hermano);
	}
}