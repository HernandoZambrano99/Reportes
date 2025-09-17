package co.com.bancolombia.model.solicitudaprobada;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SolicitudAprobada {
    private Integer solicitudId;
    private String estado;
    private String tipoPrestamo;
    private Integer plazo;
    private BigDecimal monto;
}
