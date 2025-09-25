package co.com.bancolombia.model.reporte.gateways;

import co.com.bancolombia.model.reporte.Reporte;
import reactor.core.publisher.Mono;

public interface ReporteNotificaRepository {
    Mono<Void> notificar(Reporte reporte);
}
