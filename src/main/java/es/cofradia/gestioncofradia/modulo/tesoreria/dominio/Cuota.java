package es.cofradia.gestioncofradia.modulo.tesoreria.dominio;

import java.math.BigDecimal;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import es.cofradia.gestioncofradia.modulo.cofradias.dominio.Cofradia;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.TipoCuota;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cuotas")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuota {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cofradia cofradia;

    private String nombre; // Ej: "Cuota Anual 2026" o "Salida Procesional 2026"
    
    private Integer anio;
    
    private BigDecimal importe;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_cuota_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private TipoCuota tipo;
    
    private boolean activa; // Para saber si es la que se está cobrando ahora
}