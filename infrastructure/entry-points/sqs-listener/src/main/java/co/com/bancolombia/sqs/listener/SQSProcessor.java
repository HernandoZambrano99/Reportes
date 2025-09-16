package co.com.bancolombia.sqs.listener;

import co.com.bancolombia.model.solicitudaprobada.SolicitudAprobada;
import co.com.bancolombia.usecase.reporte.ReporteUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Log4j2
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final ObjectMapper objectMapper;
    private final ReporteUseCase reporteUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        System.out.println(message.body());
        return Mono.fromCallable(() -> {
                    String body = message.body();
                    return objectMapper.readValue(body, SolicitudAprobada.class);
                })
                .flatMap(reporteUseCase::procesarSolicitudAprobada)
                .onErrorResume(e -> {
                    log.error("[SQS] Error procesando mensaje", e);
                    return Mono.empty();
                });
    }



}
