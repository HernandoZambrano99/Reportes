package co.com.bancolombia.model.reporte;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.logging.Logger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Reporte {

    private String id;
    private Long totalApprovedCount;
    private BigDecimal totalApprovedAmount;
}
