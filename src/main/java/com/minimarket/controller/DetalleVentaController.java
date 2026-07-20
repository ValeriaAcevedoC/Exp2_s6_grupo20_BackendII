package com.minimarket.controller;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.service.DetalleVentaService;
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
@RequestMapping("/api/detalle-ventas")
@Tag(name = "Detalles de venta", description = "Endpoints para la gestion de detalles asociados a ventas")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @GetMapping
    @Operation(
            summary = "Listar detalles de venta",
            description = "Obtiene todos los detalles de venta registrados en el sistema Minimarket Plus."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de detalles de venta obtenida correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<DetalleVenta> listarDetalleVentas() {
        return detalleVentaService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalle de venta por ID",
            description = "Busca un detalle de venta especifico utilizando su identificador unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de venta encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Detalle de venta no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<DetalleVenta> obtenerDetalleVentaPorId(
            @Parameter(description = "ID del detalle de venta a buscar", example = "1")
            @PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        if (detalleVenta != null) {
            return ResponseEntity.ok(detalleVenta);
        }

        throw new ResourceNotFoundException("Detalle de venta no encontrado con id: " + id);
    }

    @PostMapping
    @Operation(
            summary = "Crear detalle de venta",
            description = "Registra un nuevo detalle asociado a una venta."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Detalle de venta creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<DetalleVenta> guardarDetalleVenta(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del detalle de venta que se desea registrar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DetalleVenta.class),
                            examples = @ExampleObject(
                                    name = "Detalle de venta valido",
                                    value = """
                                            {
                                              "venta": {
                                                "id": 1
                                              },
                                              "producto": {
                                                "id": 1
                                              },
                                              "cantidad": 2,
                                              "precio": 1590.0
                                            }
                                            """
                            )
                    )
            )
            @RequestBody DetalleVenta detalleVenta) {
        DetalleVenta detalleVentaCreado = detalleVentaService.save(detalleVenta);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(detalleVentaCreado.getId())
                .toUri();

        return ResponseEntity.created(location).body(detalleVentaCreado);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar detalle de venta",
            description = "Actualiza la informacion de un detalle de venta existente segun su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de venta actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Detalle de venta no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<DetalleVenta> actualizarDetalleVenta(
            @Parameter(description = "ID del detalle de venta a actualizar", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del detalle de venta",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DetalleVenta.class),
                            examples = @ExampleObject(
                                    name = "Detalle de venta actualizado",
                                    value = """
                                            {
                                              "venta": {
                                                "id": 1
                                              },
                                              "producto": {
                                                "id": 1
                                              },
                                              "cantidad": 3,
                                              "precio": 1590.0
                                            }
                                            """
                            )
                    )
            )
            @RequestBody DetalleVenta detalleVenta) {
        DetalleVenta existente = detalleVentaService.findById(id);
        if (existente != null) {
            detalleVenta.setId(id);
            return ResponseEntity.ok(detalleVentaService.save(detalleVenta));
        }
        throw new ResourceNotFoundException("Detalle de venta no encontrado con id: " + id);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar detalle de venta",
            description = "Elimina un detalle de venta utilizando su identificador unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle de venta eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Detalle de venta no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarDetalleVenta(
            @Parameter(description = "ID del detalle de venta a eliminar", example = "1")
            @PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        if (detalleVenta != null) {
            detalleVentaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("Detalle de venta no encontrado con id: " + id);
    }
}
