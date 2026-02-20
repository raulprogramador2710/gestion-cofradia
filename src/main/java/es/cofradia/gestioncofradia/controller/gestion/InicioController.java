package es.cofradia.gestioncofradia.controller.gestion;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.cofradia.gestioncofradia.model.Hermano;
import es.cofradia.gestioncofradia.model.Usuario;
import es.cofradia.gestioncofradia.model.UsuarioCofradia;
import es.cofradia.gestioncofradia.repository.UsuarioRepository;
import es.cofradia.gestioncofradia.service.HermanoService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InicioController {

    private final HermanoService hermanoService;
    private final UsuarioRepository usuarioRepo;

    @GetMapping({"/gestion/inicio", "/gestion/dashboard"})
    public String Inicio(Model model, Principal principal) {

        if (principal != null) {
            usuarioRepo.findByUsuario(principal.getName()).ifPresent((Usuario u) -> {
                if (!u.getUsuarioCofradias().isEmpty()) {
                    UsuarioCofradia primeraAsociacion = u.getUsuarioCofradias().iterator().next();
                    model.addAttribute("cofradia", primeraAsociacion.getCofradia());
                    model.addAttribute("rolUsuario", primeraAsociacion.getRol().getDescripcion());
                }
                model.addAttribute("usuarioLogueado", u.getUsuario());
            });
        }

        List<Hermano> hermanos = hermanoService.listarTodos();
        model.addAttribute("totalHermanos", hermanos.size());

        // --- CORRECCIÓN DE LOS MAPAS PARA LOS GRÁFICOS ---
        
        // 1. Por Estado (usamos getDescripcion())
        Map<String, Integer> hermanosPorEstado = hermanos.stream()
                .map(h -> h.getEstado() != null ? h.getEstado().getCodigoVisual() : "Sin Estado")
                .collect(Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        // 2. Por Forma de Pago (usamos getDescripcion())
        Map<String, Integer> hermanosPorFormaPago = hermanos.stream()
                .map(h -> h.getFormaPago() != null ? h.getFormaPago().getCodigoVisual() : "Sin Forma de Pago")
                .collect(Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        // 3. Por Forma de Comunicación (usamos getDescripcion())
        Map<String, Integer> hermanosPorFormaComunicacion = hermanos.stream()
                .map(h -> h.getFormaComunicacion() != null ? h.getFormaComunicacion().getCodigoVisual() : "Sin Comunicación")
                .collect(Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        model.addAttribute("hermanosPorEstado", hermanosPorEstado);
        model.addAttribute("hermanosPorFormaPago", hermanosPorFormaPago);
        model.addAttribute("hermanosPorFormaComunicacion", hermanosPorFormaComunicacion);

        model.addAttribute("totalNotificaciones", 0);
        model.addAttribute("tareasProximas", List.of());

        return "gestion/inicio";
    }
}