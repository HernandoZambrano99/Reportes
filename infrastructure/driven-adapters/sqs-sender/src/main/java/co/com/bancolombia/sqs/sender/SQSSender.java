package co.com.bancolombia.sqs.sender;

import co.com.bancolombia.model.reporte.Reporte;
import co.com.bancolombia.model.reporte.gateways.ReporteNotificaRepository;
import co.com.bancolombia.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements ReporteNotificaRepository {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> notificar(Reporte reporte) {
        return Mono.fromCallable(() -> {
                    // Serializamos el reporte a JSON
                    String json = objectMapper.writeValueAsString(reporte);
                    log.info("Enviando reporte diario a SQS: {}", json);
                    return json;
                })
                .flatMap(this::send)
                .doOnNext(messageId -> log.info("Reporte enviado a SQS con id {}", messageId))
                .then();
    }
}
