package co.com.bancolombia.scheduler;

import co.com.bancolombia.usecase.reporte.ReporteUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReporteScheduler {

    private final ReporteUseCase reporteUseCase;

//    @Scheduled(cron = "0 0 2 * * *")
    @Scheduled(cron = "0 */1 * * * *")
    public void generarReporte() {
        reporteUseCase.enviarReporteTotal()
                .doOnError(e -> log.error("Error en envío de reporte total", e))
                .subscribe();
    }
}
