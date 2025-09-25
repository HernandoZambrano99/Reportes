package co.com.bancolombia.usecase.reporte;

import co.com.bancolombia.model.reporte.Reporte;
import co.com.bancolombia.model.reporte.gateways.ReporteNotificaRepository;
import co.com.bancolombia.model.reporte.gateways.ReporteRepository;
import co.com.bancolombia.model.solicitudaprobada.SolicitudAprobada;
import co.com.bancolombia.usecase.reporte.constants.Constants;
import co.com.bancolombia.usecase.reporte.exception.ReporteReadException;
import co.com.bancolombia.usecase.reporte.exception.SolicitudAprobadaInvalidaException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class ReporteUseCase {

    private static final Logger logger = Logger.getLogger(ReporteUseCase.class.getName());
    private final ReporteRepository reporteRepository;
    private static final String COUNTER_ID = Constants.COUNTER_ID;
    private final ReporteNotificaRepository reporteNotificaRepository;

    public Mono<Void> procesarSolicitudAprobada(SolicitudAprobada solicitudAprobada) {
        if (solicitudAprobada == null
                || solicitudAprobada.getMonto() == null
                || solicitudAprobada.getMonto().compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new SolicitudAprobadaInvalidaException(Constants.DATOS_INVALIDOS_SOLICITUD));
        }
        logger.info(Constants.LOG_PROCESAR_SOLICITUD + solicitudAprobada.getSolicitudId());
        return reporteRepository.increment(COUNTER_ID, 1L, solicitudAprobada.getMonto())
                .doOnSuccess(v -> logger.info(Constants.LOG_REPORTE_INCREMENTADO))
                .doOnError(e -> logger.warning(Constants.LOG_REPORTE_INCREMENT_ERROR + e.getMessage()));
    }

    public Mono<Reporte> obtenerTotal() {
        return reporteRepository.getReporte(COUNTER_ID)
                .doOnSubscribe(s -> logger.info(Constants.LOG_OBTENIENDO_REPORTE + COUNTER_ID))
                .onErrorResume(e -> {
                    logger.warning(Constants.LOG_ERROR_OBTENER_REPORTE + e.getMessage());
                    return Mono.error(new ReporteReadException(COUNTER_ID, e.getMessage(), e));
                });
    }

    public Mono<Void> enviarReporteTotal(){
        return obtenerTotal()
                .flatMap(reporte -> {
                    logger.info("Enviando reporte total a la cola: " + reporte);
                    return reporteNotificaRepository.notificar(reporte);
                })
                .doOnSuccess(v -> logger.info("Reporte total notificado correctamente"))
                .doOnError(e -> logger.warning("Error notificando reporte total: " + e.getMessage()));
    }
}
