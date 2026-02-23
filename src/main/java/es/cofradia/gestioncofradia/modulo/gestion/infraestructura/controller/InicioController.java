package es.cofradia.gestioncofradia.modulo.gestion.infraestructura.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.cofradia.gestioncofradia.modulo.hermanos.aplicacion.HermanoService;
import es.cofradia.gestioncofradia.modulo.hermanos.dominio.Hermano;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.Usuario;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.UsuarioCofradia;
import es.cofradia.gestioncofradia.modulo.usuarios.infraestructura.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InicioController {

    private final HermanoService hermanoService;
    private final UsuarioRepository usuarioRepo;

    @GetMapping({"/gestion/inicio", "/gestion/dashboard"})
    public String Inicio(Model model, Principal principal) {

    	List<Hermano> hermanos = List.of();

        if (principal != null) {
            // Buscamos al usuario
            Optional<Usuario> userOpt = usuarioRepo.findByUsuario(principal.getName());
            
            if (userOpt.isPresent()) {
                Usuario u = userOpt.get();
                model.addAttribute("usuarioLogueado", u.getUsuario());

                if (!u.getUsuarioCofradias().isEmpty()) {
                    // 2. Obtenemos la cofradía activa del usuario
                    UsuarioCofradia primeraAsociacion = u.getUsuarioCofradias().iterator().next();
                    Long cofradiaId = primeraAsociacion.getCofradia().getId();
                    
                    model.addAttribute("cofradia", primeraAsociacion.getCofradia());
                    model.addAttribute("rolUsuario", primeraAsociacion.getRol().getDescripcion());

                    // 3. ¡AQUÍ PASAMOS EL ID! Filtramos hermanos por esta cofradía
                    hermanos = hermanoService.listarPorCofradia(cofradiaId);
                }
            }
        }
        
        model.addAttribute("totalHermanos", hermanos.size());
        
        //Por Situacion
        Map<String, Integer> hermanosPorSituacion = hermanos.stream()
                .map(h -> h.getSituacion() != null ? h.getSituacion().getCodigoVisual() : "Sin Situación")
                .collect(Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

      //Por Situacion Pago
        Map<String, Integer> hermanosPorSituacionPago = hermanos.stream()
                .map(h -> h.getSituacionPago() != null ? h.getSituacionPago().getCodigoVisual() : "Sin Estado de Pago")
                .collect(Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        //Por Forma de Pago
        Map<String, Integer> hermanosPorFormaPago = hermanos.stream()
                .map(h -> h.getFormaPago() != null ? h.getFormaPago().getCodigoVisual() : "Sin Forma de Pago")
                .collect(Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        //Por Forma de Comunicación
        Map<String, Integer> hermanosPorFormaComunicacion = hermanos.stream()
                .map(h -> h.getFormaComunicacion() != null ? h.getFormaComunicacion().getCodigoVisual() : "Sin Comunicación")
                .collect(Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        model.addAttribute("hermanosPorSituacion", hermanosPorSituacion);
        model.addAttribute("hermanosPorSituacionPago", hermanosPorSituacionPago);
        model.addAttribute("hermanosPorFormaPago", hermanosPorFormaPago);
        model.addAttribute("hermanosPorFormaComunicacion", hermanosPorFormaComunicacion);

        model.addAttribute("totalNotificaciones", 0);
        model.addAttribute("tareasProximas", List.of());

        return "gestion/inicio";
    }
}