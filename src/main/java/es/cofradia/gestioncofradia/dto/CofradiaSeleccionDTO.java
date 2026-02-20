package es.cofradia.gestioncofradia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CofradiaSeleccionDTO {
	
    private Long usuarioCofradiaId;
    private Long cofradiaId;
    private String nombreCofradia;
    private String nombreRol;
    
}