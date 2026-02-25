package es.cofradia.gestioncofradia.modulo.tesoreria.dominio;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import es.cofradia.gestioncofradia.modulo.cofradias.dominio.Cofradia;
import es.cofradia.gestioncofradia.modulo.hermanos.dominio.Hermano;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.FormaPago;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.SituacionPagoHermano;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cuotas_hermanos",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cuota_hermano",
        columnNames = {"cuota_id", "hermano_id"}
    )
)
@Audited
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class CuotaHermano {
	
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuota_id", nullable = false)
    private Cuota cuota;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cofradia_id", nullable = false)
    private Cofradia cofradia; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hermano_id", nullable = false)
    private Hermano hermano;

    private BigDecimal importeFinal; // Por si hay descuento respecto al importe

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "situacion_pago_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private SituacionPagoHermano situacionPago; // Reutilizas la que ya tienes

    private LocalDate fechaPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forma_pago_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private FormaPago formaPago;

    private String referenciaPdf;

    @Column(length = 500)
    private String observaciones;

    private String generadoPor; // "SECRETARIO" o "PORTAL"
}