package es.cofradia.gestioncofradia.modulo.maestras.dominio;

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
@Table(name = "tipos_cuota")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoCuota {
	
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String codigo;
      
    private String descripcion;
    
    private String codigoVisual; 
}
