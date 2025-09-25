package co.com.bancolombia.usecase.reporte;

import co.com.bancolombia.model.reporte.Reporte;
import co.com.bancolombia.model.reporte.gateways.ReporteNotificaRepository;
import co.com.bancolombia.model.reporte.gateways.ReporteRepository;
import co.com.bancolombia.model.solicitudaprobada.SolicitudAprobada;
import co.com.bancolombia.usecase.reporte.constants.Constants;
import co.com.bancolombia.usecase.reporte.exception.ReporteReadException;
import co.com.bancolombia.usecase.reporte.exception.SolicitudAprobadaInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReporteUseCaseTest {

    private ReporteRepository reporteRepository;
    private ReporteUseCase useCase;
    private ReporteNotificaRepository reporteNotificaRepository;

    @BeforeEach
    void setUp() {
        reporteRepository = Mockito.mock(ReporteRepository.class);
        reporteNotificaRepository = Mockito.mock(ReporteNotificaRepository.class);
        useCase = new ReporteUseCase(reporteRepository, reporteNotificaRepository);
    }

    @Test
    void procesarSolicitudAprobada_ok() {
        SolicitudAprobada solicitud = new SolicitudAprobada();
        solicitud.setSolicitudId(123);
        solicitud.setMonto(BigDecimal.valueOf(5000));
        when(reporteRepository.increment(any(), anyLong(), any(BigDecimal.class)))
                .thenReturn(Mono.empty());
        StepVerifier.create(useCase.procesarSolicitudAprobada(solicitud))
                .verifyComplete();
        verify(reporteRepository).increment(Constants.COUNTER_ID, 1L, solicitud.getMonto());
    }

    @Test
    void procesarSolicitudAprobada_invalida() {
        SolicitudAprobada solicitud = new SolicitudAprobada();
        solicitud.setSolicitudId(123);
        solicitud.setMonto(BigDecimal.valueOf(-10));
        StepVerifier.create(useCase.procesarSolicitudAprobada(solicitud))
                .expectError(SolicitudAprobadaInvalidaException.class)
                .verify();
        verifyNoInteractions(reporteRepository);
    }

    @Test
    void obtenerTotal_ok() {
        Reporte reporte = new Reporte(Constants.COUNTER_ID, 10L, BigDecimal.valueOf(1000));
        when(reporteRepository.getReporte(Constants.COUNTER_ID))
                .thenReturn(Mono.just(reporte));
        StepVerifier.create(useCase.obtenerTotal())
                .expectNext(reporte)
                .verifyComplete();
        verify(reporteRepository).getReporte(Constants.COUNTER_ID);
    }

    @Test
    void obtenerTotal_error() {
        when(reporteRepository.getReporte(Constants.COUNTER_ID))
                .thenReturn(Mono.error(new RuntimeException("DB error")));
        StepVerifier.create(useCase.obtenerTotal())
                .expectError(ReporteReadException.class)
                .verify();
        verify(reporteRepository).getReporte(Constants.COUNTER_ID);
    }

    @Test
    @DisplayName("enviarReporteTotal - ok")
    void enviarReporteTotal_ok() {

        Reporte reporte = new Reporte(Constants.COUNTER_ID, 10L, BigDecimal.valueOf(1000));
        when(reporteRepository.getReporte(Constants.COUNTER_ID)).thenReturn(Mono.just(reporte));
        when(reporteNotificaRepository.notificar(reporte)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.enviarReporteTotal())
                .verifyComplete();

        verify(reporteRepository).getReporte(Constants.COUNTER_ID);
        verify(reporteNotificaRepository).notificar(reporte);
    }

    @Test
    @DisplayName("enviarReporteTotal - error al obtener reporte")
    void enviarReporteTotal_errorObteniendoReporte() {
        when(reporteRepository.getReporte(Constants.COUNTER_ID))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(useCase.enviarReporteTotal())
                .expectError(ReporteReadException.class)
                .verify();

        verify(reporteRepository).getReporte(Constants.COUNTER_ID);
        verifyNoInteractions(reporteNotificaRepository);
    }

    @Test
    @DisplayName("enviarReporteTotal - error al notificar reporte")
    void enviarReporteTotal_errorNotificandoReporte() {
        Reporte reporte = new Reporte(Constants.COUNTER_ID, 10L, BigDecimal.valueOf(1000));
        when(reporteRepository.getReporte(Constants.COUNTER_ID)).thenReturn(Mono.just(reporte));
        when(reporteNotificaRepository.notificar(reporte))
                .thenReturn(Mono.error(new RuntimeException("Notification error")));

        StepVerifier.create(useCase.enviarReporteTotal())
                .expectError(RuntimeException.class)
                .verify();

        verify(reporteRepository).getReporte(Constants.COUNTER_ID);
        verify(reporteNotificaRepository).notificar(reporte);
    }

}
