package es.cofradia.gestioncofradia.modulo.usuarios.dominio;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import es.cofradia.gestioncofradia.modulo.cofradias.dominio.Cofradia;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "usuario", unique = true, nullable = false, length = 100)
    private String usuario;

    @Column(name = "clave", nullable = false)
    @NotAudited
    private String clave;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UsuarioCofradia> usuarioCofradias = new HashSet<>();

    /* ---------- Helpers útiles ---------- */

    public Set<Cofradia> getCofradias() {
        if (usuarioCofradias == null)
            return Collections.emptySet();
        return usuarioCofradias.stream()
                .map(UsuarioCofradia::getCofradia)
                .collect(Collectors.toSet());
    }

    public void addCofradiaAssociation(UsuarioCofradia uc) {
        uc.setUsuario(this);
        usuarioCofradias.add(uc);
    }

    public void removeCofradiaAssociation(UsuarioCofradia uc) {
        usuarioCofradias.remove(uc);
        uc.setUsuario(null);
    }

    public UsuarioCofradia findAssociationFor(Cofradia cofradia) {
        return usuarioCofradias.stream()
                .filter(uc -> uc.getCofradia().getId().equals(cofradia.getId()))
                .findFirst()
                .orElse(null);
    }
}