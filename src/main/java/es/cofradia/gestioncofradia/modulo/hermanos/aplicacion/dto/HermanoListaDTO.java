package es.cofradia.gestioncofradia.modulo.hermanos.aplicacion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class HermanoListaDTO {
	
    private Long id;
    private Integer numHermano;
    private String nombre;
    private String apellidos;
    private String dni;
    private String situacionCodigo;
    private String situacionVisual;
    private String pagoCodigo;
    private String pagoVisual;
    private String tipoParticipacionNombre;
}