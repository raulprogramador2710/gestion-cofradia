package es.cofradia.gestioncofradia.config;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.cofradia.gestioncofradia.model.Cofradia;
import es.cofradia.gestioncofradia.model.Hermano;
import es.cofradia.gestioncofradia.model.Usuario;
import es.cofradia.gestioncofradia.model.UsuarioCofradia;
import es.cofradia.gestioncofradia.model.maestras.EstadoHermano;
import es.cofradia.gestioncofradia.model.maestras.FormaComunicacion;
import es.cofradia.gestioncofradia.model.maestras.FormaPago;
import es.cofradia.gestioncofradia.model.maestras.RolCofradia;
import es.cofradia.gestioncofradia.repository.CofradiaRepository;
import es.cofradia.gestioncofradia.repository.HermanoRepository;
import es.cofradia.gestioncofradia.repository.UsuarioCofradiaRepository;
import es.cofradia.gestioncofradia.repository.UsuarioRepository;
import es.cofradia.gestioncofradia.repository.maestras.EstadoHermanoRepository;
import es.cofradia.gestioncofradia.repository.maestras.FormaComunicacionRepository;
import es.cofradia.gestioncofradia.repository.maestras.FormaPagoRepository;
import es.cofradia.gestioncofradia.repository.maestras.RolCofradiaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

	private final CofradiaRepository cofradiaRepo;
    private final UsuarioRepository usuarioRepo;
    private final HermanoRepository hermanoRepo;
    private final UsuarioCofradiaRepository usuarioCofradiaRepo;
    private final EstadoHermanoRepository estadoRepo;
    private final FormaPagoRepository pagoRepo;
    private final FormaComunicacionRepository comunicacionRepo;
    private final RolCofradiaRepository rolRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
    	Cofradia expiracion = cofradiaRepo.findByNombre("Cofradia de la Expiración")
                .orElseGet(() -> {
                    Cofradia c = new Cofradia();
                    c.setNombre("Cofradia de la Expiración");
                    c.setNombreCompleto("Cofradia del Santísimo Cristo de la Expiración, Señor de las Penas, y María Santísima de los Dolores");
                    c.setCif("G12345678");
                    c.setSlug("expiracion");
                    c.setColorPrincipal1("#000000");
                    c.setColorPrincipal2("#ffffff");
                    c.setColorSecundario("#ffd700");
                    c.setUsarColoresPersonalizados(Boolean.TRUE);
                    return cofradiaRepo.save(c);
                });

        Cofradia prendimiento = cofradiaRepo.findByNombre("Hermandad del prendimiento")
                .orElseGet(() -> {
                    Cofradia c = new Cofradia();
                    c.setNombre("Hermandad del prendimiento");
                    c.setNombreCompleto("Hermandad del prendimiento");
                    c.setCif("G12345677");
                    c.setSlug("prendimiento");
                    c.setColorPrincipal1("#800040");
                    c.setColorPrincipal2("#FDFBD4");
                    c.setColorSecundario(null);
                    c.setUsarColoresPersonalizados(Boolean.TRUE);
                    return cofradiaRepo.save(c);
                });

        System.out.println(">> Cofradías aseguradas.");
        
        // --- ROLES MAESTROS (idempotente por codigo) ---
        createRolSiNoExiste("ADMIN", "Administrador");
        createRolSiNoExiste("HM", "Hermano mayor");
        createRolSiNoExiste("TES", "Tesorero");
        createRolSiNoExiste("SEC", "Secretario");
        createRolSiNoExiste("RRSS", "Redes sociales");
        createRolSiNoExiste("HER", "Hermano");
        System.out.println(">> Roles asegurados.");
        
        

        // 2. Crear Usuario ADMIN inicial (si no existe)
     // Admin Expiracion
        String userExp = "expiracion_admin";
        if (usuarioRepo.findByUsuario(userExp).isEmpty()) {
            Usuario adminExp = new Usuario();
            adminExp.setUsuario(userExp);
            adminExp.setClave(passwordEncoder.encode("Frakyx_es10"));
            adminExp = usuarioRepo.save(adminExp);

            RolCofradia rolPres = rolRepo.findByCodigo("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("Role ADMIN no existe"));

            UsuarioCofradia uc1 = UsuarioCofradia.builder()
                    .usuario(adminExp)
                    .cofradia(expiracion)
                    .rol(rolPres)
                    .build();
            usuarioCofradiaRepo.save(uc1);
        }

        // Admin Prendimiento
        String userPre = "prendimiento_admin";
        if (usuarioRepo.findByUsuario(userPre).isEmpty()) {
            Usuario adminPre = new Usuario();
            adminPre.setUsuario(userPre);
            adminPre.setClave(passwordEncoder.encode("Frakyx_es10"));
            adminPre = usuarioRepo.save(adminPre);

            RolCofradia rolPres = rolRepo.findByCodigo("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("Role ADMIN no existe"));

            UsuarioCofradia uc2 = UsuarioCofradia.builder()
                    .usuario(adminPre)
                    .cofradia(prendimiento)
                    .rol(rolPres)
                    .build();
            usuarioCofradiaRepo.save(uc2);
        }

        System.out.println(">> Usuarios administradores creados/asociados.");

        // 3. Cargar Estados
        if (estadoRepo.count() == 0) {
            saveEstado("ACTIVO", "Hermano que esta al corriente de pago", "Activo");
            saveEstado("FALLECIDO_ACTIVO", "Hermano fallecido que esta al corriente de pago", "Fallecido activo");
            saveEstado("BAJA", "Baja definitiva", "Baja");
            saveEstado("NO_PAGADO", "Hermano que no ha pagado la cuota actual", "No pagado");
            saveEstado("FALLECIDO_NO_PAGADO", "Hermano fallecido que no ha pagado la cuota actual", "Fallecido no pagado");
            saveEstado("FALLECIDO", "Hermano que ha fallecido", "Fallecido");
            System.out.println(">> Estados cargados.");
        }

        // 4. Cargar Formas de Pago
        if (pagoRepo.count() == 0) {
            savePago("TRANSFERENCIA", "Transferencia bancaria", "Transferencia");
            savePago("DOMICILIACION", "Domiciliación bancaria", "Domiciliación");
            savePago("EFECTIVO", "Pago en metálico", "Efectivo");
            System.out.println(">> Formas de pago cargadas.");
        }

        // 5. Cargar Formas de Comunicación
        if (comunicacionRepo.count() == 0) {
            saveComunicacion("WHATSAPP", "Mensaje por el grupo oficial", "Whatsapp");
            saveComunicacion("EMAIL", "Correo electrónico", "Email");
            saveComunicacion("POSTAL", "Carta postal", "Carta postal");
            System.out.println(">> Formas de comunicación cargadas.");
        }
        
        
        
     // --- CREAR UN HERMANO DE PRUEBA EN CADA COFRADÍA ---
        String dniPrueba = "54119089C";

        // Hermano en Expiración
        Cofradia exp = cofradiaRepo.findByNombre("Cofradia de la Expiración").orElseThrow();
        if (hermanoRepo.findByDniAndCofradiaId(dniPrueba, exp.getId()).isEmpty()) {
            EstadoHermano estadoActivo = estadoRepo.findByCodigo("ACTIVO").orElseThrow();
            FormaPago pagoTranseferencia = pagoRepo.findByCodigo("TRANSFERENCIA").orElseThrow();
            FormaComunicacion comWhatsapp = comunicacionRepo.findByCodigo("WHATSAPP").orElseThrow();

            Hermano hermano = new Hermano();
            hermano.setCofradia(exp);
            hermano.setEstado(estadoActivo);
            hermano.setFormaPago(pagoTranseferencia);
            hermano.setFormaComunicacion(comWhatsapp);
            
            hermano.setNumHermano(1);
            hermano.setDni(dniPrueba);
            hermano.setNombre("Raúl");
            hermano.setApellidos("González Bernal");
            hermano.setEmail("raul.27101995@gmail.com");
            hermano.setTelefono("722521995");
            hermano.setDireccion("Calle Isaac Peral 4 2F");
            hermano.setLocalidad("Adra");
            hermano.setFechaNacimiento(LocalDate.of(1995, 10, 27));
            hermano.setFechaInicioCofradia(2014);
            hermano.setFechaUltimoPago(2026);
            hermano.setIban("ES123456789132456789");
            hermano.setLopd(true);

            hermanoRepo.save(hermano);
            System.out.println(">> Hermano de prueba creado en Expiración.");
        }

        // Hermano en Prendimiento
        Cofradia pre = cofradiaRepo.findByNombre("Hermandad del prendimiento").orElseThrow();
        if (hermanoRepo.findByDniAndCofradiaId(dniPrueba, pre.getId()).isEmpty()) {
            EstadoHermano estadoActivo = estadoRepo.findByCodigo("ACTIVO").orElseThrow();
            FormaPago pagoTranseferencia = pagoRepo.findByCodigo("TRANSFERENCIA").orElseThrow();
            FormaComunicacion comWhatsapp = comunicacionRepo.findByCodigo("WHATSAPP").orElseThrow();

            Hermano hermano = new Hermano();
            hermano.setCofradia(pre);
            hermano.setEstado(estadoActivo);
            hermano.setFormaPago(pagoTranseferencia);
            hermano.setFormaComunicacion(comWhatsapp);
            
            hermano.setNumHermano(1);
            hermano.setDni(dniPrueba);
            hermano.setNombre("Raúl");
            hermano.setApellidos("González Bernal");
            hermano.setEmail("raul.27101995@gmail.com");
            hermano.setTelefono("722521995");
            hermano.setDireccion("Calle Isaac Peral 4 2F");
            hermano.setLocalidad("Adra");
            hermano.setFechaNacimiento(LocalDate.of(1995, 10, 27));
            hermano.setFechaInicioCofradia(2023);
            hermano.setFechaUltimoPago(2026);
            hermano.setIban("ES123456789132456789");
            hermano.setLopd(true);

            hermanoRepo.save(hermano);
            System.out.println(">> Hermano de prueba creado en Prendimiento.");
        }
        
        
        
    }

    private void createRolSiNoExiste(String codigo, String descripcion) {
        Optional<RolCofradia> existing = rolRepo.findByCodigo(codigo);
        if (existing.isEmpty()) {
            RolCofradia r = RolCofradia.builder()
                    .codigo(codigo)
                    .descripcion(descripcion)
                    .build();
            rolRepo.save(r);
        }
    }

    private void saveEstado(String cod, String desc, String codVis) {
        EstadoHermano e = new EstadoHermano();
        e.setCodigo(cod);
        e.setDescripcion(desc);
        e.setCodigoVisual(codVis);
        estadoRepo.save(e);
    }

    private void savePago(String cod, String desc, String codVis) {
        FormaPago f = new FormaPago();
        f.setCodigo(cod);
        f.setDescripcion(desc);
        f.setCodigoVisual(codVis);
        pagoRepo.save(f);
    }

    private void saveComunicacion(String cod, String desc, String codVis) {
        FormaComunicacion c = new FormaComunicacion();
        c.setCodigo(cod);
        c.setDescripcion(desc);
        c.setCodigoVisual(codVis);
        comunicacionRepo.save(c);
    }
}