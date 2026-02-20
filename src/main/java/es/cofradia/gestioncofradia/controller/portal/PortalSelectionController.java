package es.cofradia.gestioncofradia.controller.portal;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.cofradia.gestioncofradia.dto.CofradiaSeleccionDTO;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/portal")
public class PortalSelectionController {

    @SuppressWarnings("unchecked")
    @GetMapping("/seleccionar-cofradia")
    public String mostrarSelector(HttpSession session, Model model) {
        // Intentamos primero el nombre nuevo "cofradias", si no existe usamos el atributo antiguo "asociacionesPendientes"
        List<CofradiaSeleccionDTO> cofradias = (List<CofradiaSeleccionDTO>) session.getAttribute("cofradias");
        if (cofradias == null) {
            cofradias = (List<CofradiaSeleccionDTO>) session.getAttribute("asociacionesPendientes");
        }

        if (cofradias == null || cofradias.isEmpty()) {
            return "redirect:/login";
        }

        model.addAttribute("cofradias", cofradias);
        return "portal/seleccionar-cofradia";
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/seleccionar-cofradia")
    public String procesarSeleccion(@RequestParam("usuarioCofradiaId") Long ucId, HttpSession session) {
        List<CofradiaSeleccionDTO> cofradias = (List<CofradiaSeleccionDTO>) session.getAttribute("cofradias");
        if (cofradias == null) {
            cofradias = (List<CofradiaSeleccionDTO>) session.getAttribute("asociacionesPendientes");
        }

        if (cofradias == null || cofradias.isEmpty()) {
            return "redirect:/login";
        }

        CofradiaSeleccionDTO seleccionada = cofradias.stream()
                .filter(dto -> Objects.equals(dto.getUsuarioCofradiaId(), ucId))
                .findFirst()
                .orElse(null);

        if (seleccionada != null) {
            session.setAttribute("cofradiaId", seleccionada.getCofradiaId());
            session.setAttribute("rolCodigo", "HER");
            session.setAttribute("nombreCofradia", seleccionada.getNombreCofradia());
            // limpiar ambas por seguridad
            session.removeAttribute("asociacionesPendientes");
            session.removeAttribute("cofradias");
            return "redirect:/portal/inicio";
        }

        return "redirect:/portal/seleccionar-cofradia?error";
    }
}