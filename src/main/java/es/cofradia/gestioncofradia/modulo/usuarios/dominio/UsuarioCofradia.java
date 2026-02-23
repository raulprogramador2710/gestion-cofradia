package es.cofradia.gestioncofradia.modulo.usuarios.dominio;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import es.cofradia.gestioncofradia.modulo.cofradias.dominio.Cofradia;
import es.cofradia.gestioncofradia.modulo.usuarios.maestras.dominio.RolCofradia;
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
import lombok.ToString;

/**
 * Entidad intermedia Usuario <-> Cofradia
 */
@Entity
@Table(name = "usuario_cofradia",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "cofradia_id"}))
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"usuario"})
public class UsuarioCofradia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cofradia_id", nullable = false)
    private Cofradia cofradia;

    /** Ahora referencia a la entidad maestra RolCofradia */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private RolCofradia rol;
}