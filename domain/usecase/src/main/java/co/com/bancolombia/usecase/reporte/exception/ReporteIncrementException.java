package co.com.bancolombia.usecase.reporte.exception;

public class ReporteIncrementException extends RuntimeException {
    public ReporteIncrementException(Integer solicitudId, String detail, Throwable cause) {
        super("Error incrementando reporte para solicitud " + solicitudId + ": " + detail, cause);
    }
}