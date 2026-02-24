package es.cofradia.gestioncofradia.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.cofradia.gestioncofradia.modulo.cofradias.dominio.Cofradia;
import es.cofradia.gestioncofradia.modulo.cofradias.infraestructura.repository.CofradiaRepository;
import es.cofradia.gestioncofradia.modulo.hermanos.dominio.Hermano;
import es.cofradia.gestioncofradia.modulo.hermanos.infraestructura.repository.HermanoRepository;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.FormaComunicacion;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.FormaPago;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.SituacionHermano;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.SituacionPagoHermano;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.TipoCuota;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.FormaComunicacionRepository;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.FormaPagoRepository;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.SituacionHermanoRepository;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.SituacionPagoHermanoRepository;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.TipoCuotaRepository;
import es.cofradia.gestioncofradia.modulo.salidas.dominio.TipoParticipacion;
import es.cofradia.gestioncofradia.modulo.salidas.infraestructura.repository.TipoParticipacionRepository;
import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.Cuota;
import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.CuotaHermano;
import es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.repository.CuotaHermanoRepository;
import es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.repository.CuotaRepository;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.Usuario;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.UsuarioCofradia;
import es.cofradia.gestioncofradia.modulo.usuarios.infraestructura.repository.UsuarioCofradiaRepository;
import es.cofradia.gestioncofradia.modulo.usuarios.infraestructura.repository.UsuarioRepository;
import es.cofradia.gestioncofradia.modulo.usuarios.maestras.dominio.RolCofradia;
import es.cofradia.gestioncofradia.modulo.usuarios.maestras.infraestructura.repository.RolCofradiaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CofradiaRepository cofradiaRepo;
    private final UsuarioRepository usuarioRepo;
    private final HermanoRepository hermanoRepo;
    private final UsuarioCofradiaRepository usuarioCofradiaRepo;
    private final SituacionHermanoRepository situacionRepo;
    private final SituacionPagoHermanoRepository situacionPagoRepo;
    private final FormaPagoRepository pagoRepo;
    private final FormaComunicacionRepository comunicacionRepo;
    private final RolCofradiaRepository rolRepo;
    private final TipoParticipacionRepository tipoParticipacionRepo;
    private final TipoCuotaRepository tipoCuotaRepo;
    private CuotaRepository cuotaRepo;
    private CuotaHermanoRepository cuotaHermanoRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        cargarCofradias();
        cargarRoles();
        cargarMaestras();
        cargarUsuarios();
        cargarHermanosDePrueba();
    }

    // =========================================================
    // BLOQUE 1: COFRADÍAS
    // =========================================================
    private void cargarCofradias() {
        cofradiaRepo.findByNombre("Cofradia de la Expiración")
                .orElseGet(() -> cofradiaRepo.save(Cofradia.builder()
                        .nombre("Cofradia de la Expiración")
                        .nombreCompleto("Cofradia del Santísimo Cristo de la Expiración, Señor de las Penas, y María Santísima de los Dolores")
                        .cif("G12345678")
                        .slug("expiracion")
                        .colorPrincipal1("#000000")
                        .colorPrincipal2("#ffffff")
                        .colorSecundario("#ffd700")
                        .usarColoresPersonalizados(Boolean.TRUE)
                        .build()));

        cofradiaRepo.findByNombre("Hermandad del prendimiento")
                .orElseGet(() -> cofradiaRepo.save(Cofradia.builder()
                        .nombre("Hermandad del prendimiento")
                        .nombreCompleto("Hermandad del prendimiento")
                        .cif("G12345677")
                        .slug("prendimiento")
                        .colorPrincipal1("#800040")
                        .colorPrincipal2("#FDFBD4")
                        .usarColoresPersonalizados(Boolean.TRUE)
                        .build()));

        System.out.println(">> Cofradías aseguradas.");
    }

    // =========================================================
    // BLOQUE 2: ROLES
    // =========================================================
    private void cargarRoles() {
        createRolSiNoExiste("ADMIN", "Administrador");
        createRolSiNoExiste("HM",    "Hermano mayor");
        createRolSiNoExiste("TES",   "Tesorero");
        createRolSiNoExiste("SEC",   "Secretario");
        createRolSiNoExiste("RRSS",  "Redes sociales");
        createRolSiNoExiste("HER",   "Hermano");
        System.out.println(">> Roles asegurados.");
    }

    // =========================================================
    // BLOQUE 3: MAESTRAS
    // =========================================================
    private void cargarMaestras() {

        if (situacionRepo.count() == 0) {
            situacionRepo.save(SituacionHermano.builder().codigo("ACTIVO").descripcion("Hermano en activo").codigoVisual("Activo").build());
            situacionRepo.save(SituacionHermano.builder().codigo("BAJA").descripcion("Hermano que ha solicitado la baja").codigoVisual("Baja").build());
            situacionRepo.save(SituacionHermano.builder().codigo("FALLE").descripcion("Hermano fallecido").codigoVisual("Fallecido").build());
            System.out.println(">> Situaciones de hermano cargadas.");
        }

        if (situacionPagoRepo.count() == 0) {
            situacionPagoRepo.save(SituacionPagoHermano.builder().codigo("AL_DIA").descripcion("Sin cuotas pendientes").codigoVisual("Al día").build());
            situacionPagoRepo.save(SituacionPagoHermano.builder().codigo("DEUDOR").descripcion("Con cuotas pendientes").codigoVisual("No pagado").build());
            situacionPagoRepo.save(SituacionPagoHermano.builder().codigo("EXENTO").descripcion("Exento de pago por protocolo").codigoVisual("Exento").build());
            System.out.println(">> Situaciones de pago cargadas.");
        }

        if (pagoRepo.count() == 0) {
            savePago("TRANSFERENCIA", "Transferencia bancaria", "Transferencia");
            savePago("DOMICILIACION", "Domiciliación bancaria", "Domiciliación");
            savePago("EFECTIVO",      "Pago en metálico",       "Efectivo");
            System.out.println(">> Formas de pago cargadas.");
        }

        if (comunicacionRepo.count() == 0) {
            saveComunicacion("WHATSAPP", "Mensaje por el grupo oficial", "Whatsapp");
            saveComunicacion("EMAIL",    "Correo electrónico",           "Email");
            saveComunicacion("POSTAL",   "Carta postal",                 "Carta postal");
            System.out.println(">> Formas de comunicación cargadas.");
        }

        if (tipoParticipacionRepo.count() == 0) {
            tipoParticipacionRepo.saveAll(List.of(
                TipoParticipacion.builder().nombre("Nazareno")  .descripcion("Sale en la procesión como nazareno").build(),
                TipoParticipacion.builder().nombre("Portador")  .descripcion("Porta el paso").build(),
                TipoParticipacion.builder().nombre("Mantilla")  .descripcion("Sale en la procesión con mantilla").build(),
                TipoParticipacion.builder().nombre("Acólito")   .descripcion("Acompaña al clero").build(),
                TipoParticipacion.builder().nombre("Monaguillo").descripcion("Monaguillo de la procesión").build()
            ));
            System.out.println(">> Tipos de participación cargados.");
        }
        
        if (tipoCuotaRepo.count() == 0) {
            tipoCuotaRepo.save(TipoCuota.builder().codigo("ANUAL").descripcion("Cuota anual de hermano").codigoVisual("Anual").build());
            tipoCuotaRepo.save(TipoCuota.builder().codigo("EXTRAORDINARIA").descripcion("Cuota extraordinaria o derrama").codigoVisual("Extraordinaria").build());
            tipoCuotaRepo.save(TipoCuota.builder().codigo("PAPELETA").descripcion("Pago por salida procesional").codigoVisual("Papeleta").build());
            System.out.println(">> Tipos de cuota cargados.");
        }
        
    }

    // =========================================================
    // BLOQUE 4: USUARIOS
    // =========================================================
    private void cargarUsuarios() {
        Cofradia expiracion   = cofradiaRepo.findByNombre("Cofradia de la Expiración").orElseThrow();
        Cofradia prendimiento = cofradiaRepo.findByNombre("Hermandad del prendimiento").orElseThrow();

        crearUsuarioSiNoExiste("expiracion_admin",   "Frakyx_es10", expiracion,   "ADMIN");
        crearUsuarioSiNoExiste("prendimiento_admin", "Frakyx_es10", prendimiento, "ADMIN");
        crearUsuarioSiNoExiste("RAUGONBER",          "Temporal01",  expiracion,   "HER");

        System.out.println(">> Usuarios creados/asociados.");
    }

    // =========================================================
    // BLOQUE 5: DATOS DE PRUEBA
    // =========================================================
    private void cargarHermanosDePrueba() {
        Cofradia expiracion   = cofradiaRepo.findByNombre("Cofradia de la Expiración").orElseThrow();
        Cofradia prendimiento = cofradiaRepo.findByNombre("Hermandad del prendimiento").orElseThrow();

        crearHermanoDePrueba(expiracion,   "54119089C", 1, 2014);
        crearHermanoDePrueba(prendimiento, "54119089C", 1, 2023);

        System.out.println(">> Hermanos de prueba asegurados.");
    }

    // =========================================================
    // MÉTODOS PRIVADOS DE APOYO
    // =========================================================
    private void createRolSiNoExiste(String codigo, String descripcion) {
        if (rolRepo.findByCodigo(codigo).isEmpty()) {
            rolRepo.save(RolCofradia.builder().codigo(codigo).descripcion(descripcion).build());
        }
    }

    private void crearUsuarioSiNoExiste(String username, String clave, Cofradia cofradia, String codigoRol) {
        if (usuarioRepo.findByUsuario(username).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsuario(username);
            usuario.setClave(passwordEncoder.encode(clave));
            usuario = usuarioRepo.save(usuario);

            RolCofradia rol = rolRepo.findByCodigo(codigoRol)
                    .orElseThrow(() -> new IllegalStateException("Rol no encontrado: " + codigoRol));

            usuarioCofradiaRepo.save(UsuarioCofradia.builder()
                    .usuario(usuario)
                    .cofradia(cofradia)
                    .rol(rol)
                    .build());
        }
    }

    private void crearHermanoDePrueba(Cofradia cofradia, String dni, Integer numHermano, Integer anioInicio) {
        if (hermanoRepo.findByDniAndCofradiaId(dni, cofradia.getId()).isEmpty()) {
            SituacionHermano activo    = situacionRepo.findByCodigo("ACTIVO").orElseThrow();

            hermanoRepo.save(Hermano.builder()
                    .cofradia(cofradia)
                    .situacion(activo)
                    .formaPago(pagoRepo.findByCodigo("TRANSFERENCIA").orElseThrow())
                    .formaComunicacion(comunicacionRepo.findByCodigo("WHATSAPP").orElseThrow())
                    .numHermano(numHermano)
                    .dni(dni)
                    .nombre("Raúl")
                    .apellidos("González Bernal")
                    .email("raul.27101995@gmail.com")
                    .telefono("722521995")
                    .direccion("Calle Isaac Peral 4 2F")
                    .localidad("Adra")
                    .fechaNacimiento(LocalDate.of(1995, 10, 27))
                    .fechaInicioCofradia(anioInicio)
                    .fechaUltimoPago(2026)
                    .iban("ES123456789132456789")
                    .lopd(true)
                    .build());
        }
    }
    
    public void cargarCuota2025ConPagoParaHermano(Hermano hermano) {
        // 1. Crear o buscar la cuota del año 2025
        Cuota cuota2025 = cuotaRepo.findByAnio(2025)
            .orElseGet(() -> {
                Cuota nuevaCuota = new Cuota();
                nuevaCuota.setAnio(2025);
                nuevaCuota.setImporte(new BigDecimal("30.00")); // Ajusta importe si quieres
                nuevaCuota.setTipoCuota(tipoCuotaRepository.findByCodigo("ANUAL").orElse(null)); // Ajusta tipo cuota
                return cuotaRepository.save(nuevaCuota);
            });

        // 2. Buscar la situación de pago "PENDIENTE" o "AL_DIA" según quieras
        SituacionPagoHermano situacionPago = situacionPagoRepo.findByCodigo("PENDIENTE")
            .orElseThrow(() -> new RuntimeException("Situación de pago 'PENDIENTE' no encontrada"));

        // 3. Crear la relación CuotaHermano para el hermano y la cuota 2025
        CuotaHermano cuotaHermano = new CuotaHermano();
        cuotaHermano.setCuota(cuota2025);
        cuotaHermano.setHermano(hermano);
        cuotaHermano.setSituacionPago(situacionPago);
        // No ponemos fechaPago porque está pendiente
        cuotaHermanoRepo.save(cuotaHermano);

        System.out.println("Cuota 2025 creada y asociada al hermano " + hermano.getNumHermano());
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