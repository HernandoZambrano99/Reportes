package co.com.bancolombia.model.reporte.gateways;

import co.com.bancolombia.model.reporte.Reporte;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ReporteRepository {
    Mono<Reporte> getReporte(String id);
    Mono<Void> increment(String id, long countDelta, BigDecimal amountDelta);
    Mono<Reporte> save(Reporte reporte);
}
