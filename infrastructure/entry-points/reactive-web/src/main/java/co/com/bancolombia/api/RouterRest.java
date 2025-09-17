package co.com.bancolombia.api;

import co.com.bancolombia.api.constants.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/reportes",
                    produces = {"application/json"},
                    method = {org.springframework.web.bind.annotation.RequestMethod.GET},
                    beanClass = Handler.class,
                    beanMethod = "getReporte",
                    operation = @Operation(
                            operationId = "getReporte",
                            summary = "Obtener reporte de solicitudes aprobadas",
                            description = "Devuelve un resumen con el total de solicitudes aprobadas y el monto total de créditos aprobados.",
                            parameters = {
                                    @Parameter(
                                            in = ParameterIn.HEADER,
                                            name = "Authorization",
                                            required = true,
                                            description = "Token Bearer JWT para autenticación")
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Reporte generado correctamente",
                                            content = @Content(schema = @Schema(
                                                    implementation = Map.class,
                                                    example = "{\n" +
                                                            "  \"" + AppConstants.FIELD_ID + "\": \"uuid\",\n" +
                                                            "  \"" + AppConstants.FIELD_TOTAL_SOLICITUDES_APROBADAS + "\": 25,\n" +
                                                            "  \"" + AppConstants.FIELD_TOTAL_MONTO_CREDITOS + "\": 1200000\n" +
                                                            "}"
                                            ))
                                    ),
                                    @ApiResponse(responseCode = "401", description = "Token inválido o no provisto"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/api/v1/reportes"), handler::getReporte);
    }
}
