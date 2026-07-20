package com.minimarket.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Publico", description = "Endpoints publicos de prueba")
public class HolaMundoController {

    @GetMapping("/public/hola")
    @Operation(
            summary = "Saludo publico",
            description = "Devuelve un mensaje publico de prueba para verificar que la API responde."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saludo obtenido correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public String holaMundo() {
        return "¡Hola Mundo!";
    }
}
