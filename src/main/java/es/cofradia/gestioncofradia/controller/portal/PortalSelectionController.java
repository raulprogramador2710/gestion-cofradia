package es.cofradia.gestioncofradia.controller.portal;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PortalSelectionController {

    @GetMapping("/portal/seleccionar-cofradia")
    public String mostrarSelector(HttpServletRequest request, Model model) {
        @SuppressWarnings("unchecked")
        List<?> asociaciones = (List<?>) request.getSession().getAttribute("asociacionesPendientes");
        model.addAttribute("asociaciones", asociaciones);
        return "portal/seleccionar-cofradia"; // plantilla Thymeleaf
    }

    @PostMapping("/portal/seleccionar-cofradia")
    public String procesarSeleccion(HttpServletRequest request, Long usuarioCofradiaId) {
        // Aquí deberías validar que usuarioCofradiaId está en la lista de asociacionesPendientes
        // Para simplificar, lo guardamos directamente en sesión
        request.getSession().setAttribute("selectedUsuarioCofradiaId", usuarioCofradiaId);

        // También puedes cargar la cofradía y rol para guardar en sesión si quieres
        // Por ahora redirigimos al portal hermano
        return "redirect:/portal/inicio";
    }
}