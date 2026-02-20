package es.cofradia.gestioncofradia.controller.portal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/portal")
public class PortalController {

    @GetMapping("/inicio")
    public String inicio() {
        return "portal/inicio"; // Esto buscará templates/portal/inicio.html
    }
}
