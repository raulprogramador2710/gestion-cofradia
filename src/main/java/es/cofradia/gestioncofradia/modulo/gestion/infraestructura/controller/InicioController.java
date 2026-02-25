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
import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.CuotaHermano;
import es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.repository.CuotaHermanoRepository;
import es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.repository.CuotaRepository;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.Usuario;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.UsuarioCofradia;
import es.cofradia.gestioncofradia.modulo.usuarios.infraestructura.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession; // Importante para la sesión
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InicioController {

    private final HermanoService hermanoService;
    private final UsuarioRepository usuarioRepo;
    private final CuotaRepository cuotaRepository;
    private final CuotaHermanoRepository cuotaHermanoRepository;

    @GetMapping({"/gestion/inicio", "/gestion/dashboard"})
    public String Inicio(Model model, Principal principal, HttpSession session) { // Añadimos HttpSession

        List<Hermano> hermanos = List.of();

        if (principal != null) {
            Optional<Usuario> userOpt = usuarioRepo.findByUsuario(principal.getName());

            if (userOpt.isPresent()) {
                Usuario u = userOpt.get();
                
                // GUARDAR EN SESIÓN PARA EL MENU (base.html)
                session.setAttribute("nombreUsuario", u.getUsuario()); // O u.getNombre() si tienes ese campo
                
                if (!u.getUsuarioCofradias().isEmpty()) {
                    UsuarioCofradia primeraAsociacion = u.getUsuarioCofradias().iterator().next();
                    Long cofradiaId = primeraAsociacion.getCofradia().getId();

                    // GUARDAR EN SESIÓN PARA EL MENU (base.html)
                    session.setAttribute("nombreCofradia", primeraAsociacion.getCofradia().getNombre());
                    session.setAttribute("rolUsuario", primeraAsociacion.getRol().getDescripcion());
                    session.setAttribute("cofradiaId", cofradiaId);

                    // Atributos para el Model (solo para inicio.html)
                    model.addAttribute("cofradia", primeraAsociacion.getCofradia());
                    model.addAttribute("rolUsuario", primeraAsociacion.getRol().getDescripcion());

                    hermanos = hermanoService.listarPorCofradia(cofradiaId);

                    // Estadística de pago desde la última cuota activa
                    Map<String, Integer> hermanosPorSituacionPago = cuotaRepository
                            .findFirstByCofradiaIdAndActivaTrueOrderByAnioDesc(cofradiaId)
                            .map(cuota -> {
                                List<CuotaHermano> cuotasHermanos = cuotaHermanoRepository.findByCuotaId(cuota.getId());
                                return cuotasHermanos.stream()
                                        .map(ch -> ch.getSituacionPago() != null
                                                ? ch.getSituacionPago().getCodigoVisual()
                                                : "Sin Estado de Pago")
                                        .collect(Collectors.groupingBy(
                                                Function.identity(),
                                                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
                            })
                            .orElse(Map.of("Sin cuota activa", 0));

                    model.addAttribute("hermanosPorSituacionPago", hermanosPorSituacionPago);
                }
            }
        }

        model.addAttribute("totalHermanos", hermanos.size());

        // Estadísticas (proseguir igual...)
        Map<String, Integer> hermanosPorSituacion = hermanos.stream()
                .map(h -> h.getSituacion() != null ? h.getSituacion().getCodigoVisual() : "Sin Situación")
                .collect(Collectors.groupingBy(Function.identity(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        Map<String, Integer> hermanosPorFormaPago = hermanos.stream()
                .map(h -> h.getFormaPago() != null ? h.getFormaPago().getCodigoVisual() : "Sin Forma de Pago")
                .collect(Collectors.groupingBy(Function.identity(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        Map<String, Integer> hermanosPorFormaComunicacion = hermanos.stream()
                .map(h -> h.getFormaComunicacion() != null ? h.getFormaComunicacion().getCodigoVisual() : "Sin Comunicación")
                .collect(Collectors.groupingBy(Function.identity(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        model.addAttribute("hermanosPorSituacion", hermanosPorSituacion);
        model.addAttribute("hermanosPorFormaPago", hermanosPorFormaPago);
        model.addAttribute("hermanosPorFormaComunicacion", hermanosPorFormaComunicacion);

        if (!model.containsAttribute("hermanosPorSituacionPago")) {
            model.addAttribute("hermanosPorSituacionPago", Map.of());
        }

        model.addAttribute("totalNotificaciones", 0);
        model.addAttribute("tareasProximas", List.of());

        return "gestion/inicio";
    }
}