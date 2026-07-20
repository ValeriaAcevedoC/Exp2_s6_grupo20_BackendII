package com.minimarket.controller;

import com.minimarket.entity.Venta;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Endpoints para la gestion de ventas del sistema Minimarket Plus")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    @Operation(
            summary = "Listar ventas",
            description = "Obtiene todas las ventas registradas en el sistema Minimarket Plus."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ventas obtenida correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<Venta> listarVentas() {
        return ventaService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener venta por ID",
            description = "Busca una venta especifica utilizando su identificador unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta encontrada correctamente"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Venta> obtenerVentaPorId(
            @Parameter(description = "ID de la venta a buscar", example = "1")
            @PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        if (venta != null) {
            return ResponseEntity.ok(venta);
        }

        throw new ResourceNotFoundException("Venta no encontrada con id: " + id);
    }

    @PostMapping
    @Operation(
            summary = "Crear venta",
            description = "Registra una nueva venta en el sistema Minimarket Plus."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Venta> guardarVenta(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la venta que se desea registrar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Venta.class),
                            examples = @ExampleObject(
                                    name = "Venta valida",
                                    value = """
                                            {
                                              "usuario": {
                                                "id": 1
                                              },
                                              "fecha": "2026-07-17T18:30:00.000Z"
                                            }
                                            """
                            )
                    )
            )
            @RequestBody Venta venta) {
        Venta ventaCreada = ventaService.save(venta);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(ventaCreada.getId())
                .toUri();

        return ResponseEntity.created(location).body(ventaCreada);
    }
}
