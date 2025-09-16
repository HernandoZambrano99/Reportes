package co.com.bancolombia.usecase.reporte;

import co.com.bancolombia.model.reporte.Reporte;
import co.com.bancolombia.model.reporte.gateways.ReporteRepository;
import co.com.bancolombia.model.solicitudaprobada.SolicitudAprobada;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class ReporteUseCase {

    private static final Logger logger = Logger.getLogger(ReporteUseCase.class.getName());
    private final ReporteRepository reporteRepository;
    private final String COUNTER_ID = "total_aprobados";

    public Mono<Void> procesarSolicitudAprobada(SolicitudAprobada solicitudAprobada) {
        logger.info("Actualizando reporte para solicitud aprobada: " + solicitudAprobada.getSolicitudId());
        return reporteRepository.increment(COUNTER_ID, 1L, solicitudAprobada.getMonto())
                .doOnSuccess(v -> logger.info("Reporte incrementado correctamente"))
                .doOnError(e -> logger.warning("Error incrementando reporte: " + e.getMessage()));
    }

    public Mono<Reporte> obtenerTotal() {
        return reporteRepository.getReporte(COUNTER_ID)
                .doOnSubscribe(s -> logger.info("Obteniendo reporte: " + COUNTER_ID))
                .onErrorResume(e -> {
                    logger.warning("Error al obtener reporte: " + e.getMessage());
                    // fallback explícito para no exponer excepción al usuario
                    return Mono.just(new Reporte(COUNTER_ID, 0L, java.math.BigDecimal.ZERO));
                });
    }
}
