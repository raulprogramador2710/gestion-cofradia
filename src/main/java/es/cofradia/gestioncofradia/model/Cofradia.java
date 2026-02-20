package es.cofradia.gestioncofradia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cofradias")
@Data
public class Cofradia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String nombreCompleto;
    
    private String cif;
    
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
