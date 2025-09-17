package co.com.bancolombia.api;

import co.com.bancolombia.api.constants.AppConstants;
import co.com.bancolombia.usecase.reporte.ReporteUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final ReporteUseCase reporteUseCase;

    public Mono<ServerResponse> getReporte(ServerRequest req) {
        String trace = UUID.randomUUID().toString();
        log.debug(AppConstants.LOG_GET_REPORTE_TRACE, trace);

        return reporteUseCase.obtenerTotal()
                .flatMap(r -> {
                    Map<String, Object> body = Map.of(
                            AppConstants.FIELD_ID, r.getId(),
                            AppConstants.FIELD_TOTAL_SOLICITUDES_APROBADAS, r.getTotalApprovedCount(),
                            AppConstants.FIELD_TOTAL_MONTO_CREDITOS, r.getTotalApprovedAmount()
                    );
                    return ServerResponse.ok().bodyValue(body);
                })
                .doOnError(e -> log.error(AppConstants.LOG_GET_REPORTE_ERROR, e));
    }
}
