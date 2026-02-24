package es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.controller;

import es.cofradia.gestioncofradia.modulo.tesoreria.aplicacion.CuotaService;
import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.Cuota;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gestion/cuotas")
@RequiredArgsConstructor
public class CuotaController {

    private final CuotaService cuotaService;

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
    public String guardarCuota(@ModelAttribute Cuota cuota, HttpSession session, RedirectAttributes ra) {
        Long cofradiaId = (Long) session.getAttribute("cofradiaId");
        cuotaService.guardar(cuota, cofradiaId);
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
}