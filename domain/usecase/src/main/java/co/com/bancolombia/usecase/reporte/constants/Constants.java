package co.com.bancolombia.usecase.reporte.constants;

public final class Constants {
    private Constants() {}

    public static final String COUNTER_ID = "total_aprobados";

    public static final String LOG_PROCESAR_SOLICITUD =
            "Actualizando reporte para solicitud aprobada: ";

    public static final String LOG_REPORTE_INCREMENTADO =
            "Reporte incrementado correctamente";

    public static final String LOG_REPORTE_INCREMENT_ERROR =
            "Error incrementando reporte: ";

    public static final String LOG_OBTENIENDO_REPORTE =
            "Obteniendo reporte: ";

    public static final String LOG_ERROR_OBTENER_REPORTE =
            "Error al obtener reporte: ";

    public static final String DATOS_INVALIDOS_SOLICITUD = "Datos inválidos para procesar solicitud aprobada";

    public static final String LOG_ENVIANDO_REPORTE_TOTAL =
            "Enviando reporte total a la cola: ";

    public static final String LOG_REPORTE_TOTAL_NOTIFICADO =
            "Reporte total notificado correctamente";

    public static final String LOG_ERROR_NOTIFICAR_REPORTE_TOTAL =
            "Error notificando reporte total: ";
}
