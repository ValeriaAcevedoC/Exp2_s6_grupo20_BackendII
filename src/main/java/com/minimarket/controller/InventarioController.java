package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.hateoas.InventarioModelAssembler;
import com.minimarket.service.InventarioService;
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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.net.URI;

@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Endpoints para la gestion de movimientos de inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private InventarioModelAssembler inventarioModelAssembler;

    @GetMapping
    @Operation(
            summary = "Listar movimientos de inventario",
            description = "Obtiene todos los movimientos de inventario registrados en el sistema Minimarket Plus."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de movimientos obtenida correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public CollectionModel<EntityModel<Inventario>> listarMovimientosDeInventario() {
        return inventarioModelAssembler.toCollectionModel(inventarioService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener movimiento de inventario por ID",
            description = "Busca un movimiento de inventario especifico utilizando su identificador unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento de inventario encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Movimiento de inventario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Inventario>> obtenerMovimientoPorId(
            @Parameter(description = "ID del movimiento de inventario a buscar", example = "1")
            @PathVariable Long id) {

        Inventario inventario = inventarioService.findById(id);

        if (inventario != null) {
            return ResponseEntity.ok(inventarioModelAssembler.toModel(inventario));
        }

        throw new ResourceNotFoundException("Movimiento de inventario no encontrado con id: " + id);
    }
    
    @PostMapping
    @Operation(
            summary = "Registrar movimiento de inventario",
            description = "Registra un nuevo movimiento de inventario en el sistema Minimarket Plus."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimiento de inventario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Inventario> registrarMovimiento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del movimiento de inventario que se desea registrar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Inventario.class),
                            examples = @ExampleObject(
                                    name = "Movimiento de inventario valido",
                                    value = """
                                            {
                                              "producto": {
                                                "id": 1
                                              },
                                              "cantidad": 10,
                                              "tipoMovimiento": "Entrada",
                                              "fechaMovimiento": "2026-07-17T18:30:00.000Z"
                                            }
                                            """
                            )
                    )
            )
            @RequestBody Inventario inventario) {
        Inventario inventarioCreado = inventarioService.save(inventario);
        URI location = inventarioModelAssembler.toSelfUri(inventarioCreado);

        return ResponseEntity.created(location).body(inventarioCreado);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar movimiento de inventario",
            description = "Actualiza la informacion de un movimiento de inventario existente segun su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento de inventario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Movimiento de inventario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Inventario> actualizarMovimiento(
            @Parameter(description = "ID del movimiento de inventario a actualizar", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del movimiento de inventario",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Inventario.class),
                            examples = @ExampleObject(
                                    name = "Movimiento de inventario actualizado",
                                    value = """
                                            {
                                              "producto": {
                                                "id": 1
                                              },
                                              "cantidad": 5,
                                              "tipoMovimiento": "Salida",
                                              "fechaMovimiento": "2026-07-17T19:00:00.000Z"
                                            }
                                            """
                            )
                    )
            )
            @RequestBody Inventario inventario) {
        Inventario existente = inventarioService.findById(id);
        if (existente != null) {
            inventario.setId(id);
            return ResponseEntity.ok(inventarioService.save(inventario));
        }
        throw new ResourceNotFoundException("Movimiento de inventario no encontrado con id: " + id);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar movimiento de inventario",
            description = "Elimina un movimiento de inventario utilizando su identificador unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movimiento de inventario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Movimiento de inventario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarMovimiento(
            @Parameter(description = "ID del movimiento de inventario a eliminar", example = "1")
            @PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario != null) {
            inventarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("Movimiento de inventario no encontrado con id: " + id);
    }
}
