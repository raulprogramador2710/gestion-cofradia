package es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.cofradia.gestioncofradia.modulo.tesoreria.aplicacion.CuotaService;
import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.Cuota;
import es.cofradia.gestioncofradia.modulo.usuarios.infraestructura.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/gestion/cuotas")
@RequiredArgsConstructor
public class CuotaController {

    private final CuotaService cuotaService;
    private final UsuarioRepository usuarioRepo;

    @GetMapping
    public String listarCuotas(Model model, HttpSession session) {
        Long cofradiaId = (Long) session.getAttribute("cofradiaId");
        model.addAttribute("cuotas", cuotaService.findByCofradiaId(cofradiaId));
        return "gestion/cuotas/lista_cuotas";
    }

    @GetMapping("/nueva")
    public String nuevaCuota(Model model) {
        model.addAttribute("cuota", new Cuota());
        model.addAttribute("tiposCuota", cuotaService.findAllTiposCuota());
        return "gestion/cuotas/form_cuota";
    }
    
    @GetMapping("/{id}")
    public String detalleCuota(@PathVariable Long id, Model model) {
        model.addAttribute("cuota", cuotaService.findById(id));
        return "gestion/cuotas/detalle_cuota";
    }

    @GetMapping("/{id}/editar")
    public String editarCuota(@PathVariable Long id, Model model) {
        model.addAttribute("cuota", cuotaService.findById(id));
        model.addAttribute("tiposCuota", cuotaService.findAllTiposCuota());
        return "gestion/cuotas/form_cuota";
    }

    @PostMapping("/guardar")
    public String guardarCuota(@ModelAttribute Cuota cuota, HttpSession session, RedirectAttributes ra, Principal principal) {
        Long cofradiaId = (Long) session.getAttribute("cofradiaId");
        String generadoPor = "SYSTEM"; // valor por defecto

        if (principal != null) {
        	 generadoPor = principal.getName();
        }
        
        cuotaService.guardar(cuota, cofradiaId, generadoPor);
        ra.addFlashAttribute("mensaje", "Cuota guardada correctamente");
        return "redirect:/gestion/cuotas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarCuota(@PathVariable Long id, RedirectAttributes ra) {
        try {
            cuotaService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Cuota eliminada correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar la cuota: puede que tenga recibos asociados.");
        }
        return "redirect:/gestion/cuotas";
    }
    
    @PostMapping("/marcar-pagada/{id}")
    @ResponseBody
    public ResponseEntity<?> marcarPagada(@PathVariable Long id) {
        try {
            cuotaService.marcarComoPagada(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}