package es.cofradia.gestioncofradia.model;

import java.time.LocalDate;

import es.cofradia.gestioncofradia.model.maestras.EstadoHermano;
import es.cofradia.gestioncofradia.model.maestras.FormaComunicacion;
import es.cofradia.gestioncofradia.model.maestras.FormaPago;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "hermanos",
	uniqueConstraints = @UniqueConstraint( name = "uk_hermano_dniCofradia", columnNames = {"dni", "cofradia_id"} )
)
@Data
public class Hermano {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numHermano;
    
    @Column(nullable = false, length = 9)
    private String dni;
    
    private String nombre;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String localidad;
    
    private LocalDate fechaNacimiento;
    private Integer fechaInicioCofradia;
    private Integer fechaUltimoPago;
    
    private String email;
    private boolean lopd;
    private String iban;

    // RELACIONES
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cofradia_id", nullable = false)
    private Cofradia cofradia;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoHermano estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "forma_pago_id", nullable = false)
    private FormaPago formaPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "forma_comunicacion_id", nullable = false)
    private FormaComunicacion formaComunicacion;
}