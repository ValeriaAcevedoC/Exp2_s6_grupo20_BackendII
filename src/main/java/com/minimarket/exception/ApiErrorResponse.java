package com.minimarket.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta estandar para errores de la API.")
public record ApiErrorResponse(
        @Schema(description = "Codigo HTTP del error.", example = "404")
        int status,

        @Schema(description = "Nombre del error HTTP.", example = "Not Found")
        String error,

        @Schema(description = "Mensaje descriptivo del error.", example = "Producto no encontrado con id: 99")
        String message,

        @Schema(description = "Ruta donde ocurrio el error.", example = "/api/productos/99")
        String path,

        @Schema(description = "Fecha y hora en que ocurrio el error.", example = "2026-07-17T23:10:00")
        LocalDateTime timestamp
) {
}
