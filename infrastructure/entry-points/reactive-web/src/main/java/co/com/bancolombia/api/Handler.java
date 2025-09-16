package co.com.bancolombia.api;

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
        log.debug("GET /api/v1/reportes trace={}", trace);

        return reporteUseCase.obtenerTotal()
                .flatMap(r -> {
                    Map<String, Object> body = Map.of(
                            "id", r.getId(),
                            "totalApprovedCount", r.getTotalApprovedCount(),
                            "totalApprovedAmount", r.getTotalApprovedAmount()
                    );
                    return ServerResponse.ok().bodyValue(body);
                })
                .doOnError(e -> log.error("Error en GET /api/v1/reportes" ))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue(Map.of("error", "Error consultando reporte")));
    }
}
