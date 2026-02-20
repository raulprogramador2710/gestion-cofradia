package es.cofradia.gestioncofradia.model;

import java.time.LocalDate;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hermanos",
		  uniqueConstraints = {
		    @UniqueConstraint(name = "uk_hermano_dniCofradia", columnNames = {"dni", "cofradia_id"}),
		    @UniqueConstraint(name = "uk_hermano_numcofr", columnNames = {"num_hermano", "cofradia_id"})
		  }
		)
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private EstadoHermano estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "forma_pago_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private FormaPago formaPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "forma_comunicacion_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private FormaComunicacion formaComunicacion;
}