package co.com.bancolombia.api.constants;

public class AppConstants {
    private AppConstants() {
    }
    public static final String BEARER = "Bearer ";
    public static final String API_REPORTE_PATH = "/api/v1/reportes";
    public static final String LOG_GET_REPORTE_TRACE = "GET " + API_REPORTE_PATH + " trace={}";
    public static final String LOG_GET_REPORTE_ERROR = "Error en GET " + API_REPORTE_PATH;
    public static final String FIELD_ID = "id";
    public static final String FIELD_TOTAL_SOLICITUDES_APROBADAS = "totalSolicitudesAprobadas";
    public static final String FIELD_TOTAL_MONTO_CREDITOS = "totalMontoCreditos";
}
