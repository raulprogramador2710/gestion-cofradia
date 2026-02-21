package es.cofradia.gestioncofradia.service;

import es.cofradia.gestioncofradia.model.Hermano;
import es.cofradia.gestioncofradia.repository.HermanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

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