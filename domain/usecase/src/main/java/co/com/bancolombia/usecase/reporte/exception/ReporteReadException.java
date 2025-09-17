package co.com.bancolombia.usecase.reporte.exception;

public class ReporteReadException extends RuntimeException {
    public ReporteReadException(String counterId, String detail, Throwable cause) {
        super("Error leyendo reporte " + counterId + ": " + detail, cause);
    }
}