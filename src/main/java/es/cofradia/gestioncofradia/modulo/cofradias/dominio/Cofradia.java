package es.cofradia.gestioncofradia.modulo.cofradias.dominio;

import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cofradias")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cofradia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String nombreCompleto;
    
    private String cif;
    
    @Column(name = "slug", unique = true, nullable = false)
    private String slug; // Ejemplo: "expiracion"
    
    // Colores (hex, con #). Aceptamos null si no hay tercer color.
    @Column(name = "color_principal_1")
    private String colorPrincipal1;

    @Column(name = "color_principal_2")
    private String colorPrincipal2;

    @Column(name = "color_secundario")
    private String colorSecundario;

    // Si false -> se usan los colores por defecto de la app (tema común).
    @Column(name = "usar_colores_personalizados")
    private Boolean usarColoresPersonalizados = Boolean.FALSE;
}
