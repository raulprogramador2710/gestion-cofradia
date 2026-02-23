package es.cofradia.gestioncofradia.modulo.salidas.dominio;

import java.time.LocalDateTime;

import org.hibernate.envers.Audited;

import es.cofradia.gestioncofradia.modulo.hermanos.dominio.Hermano;
import jakarta.persistence.Column;
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
@Table(name = "papeletas_sitio")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PapeletaSitio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hermano_id", nullable = false)
    private Hermano hermano;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_participacion_id", nullable = false)
    private TipoParticipacion tipoParticipacion;
    
    private boolean incluidaEnCuota; // Si es true, el importe será 0 o informativo

    @Column(nullable = false)
    private Integer anio; // Ej: 2026

    private String puesto; // Ej: "Tramo 1, Pareja 4" o "Contraguía"
    
    private LocalDateTime fechaEmision;
    
    private boolean pagada;
    
    private Double importePagado;

    // Ejemplo de método DDD: ¿Es de este año?
    public boolean esDeAnioActual() {
        return this.anio == java.time.Year.now().getValue();
    }
    
    // Método DDD para saber si está "resuelta" económicamente
    public boolean estaAlCorriente() {
        return incluidaEnCuota || pagada;
    }
}