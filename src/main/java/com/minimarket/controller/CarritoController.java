package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.hateoas.CarritoModelAssembler;
import com.minimarket.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.net.URI;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Endpoints para la gestión del carrito de compras del sistema Minimarket Plus")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CarritoModelAssembler carritoModelAssembler;

    @GetMapping
    @Operation(
            summary = "Listar carrito",
            description = "Obtiene todos los productos o registros agregados al carrito de compras."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito listado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public CollectionModel<EntityModel<Carrito>> listarCarrito() {
        return carritoModelAssembler.toCollectionModel(carritoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener registro del carrito por ID",
            description = "Busca un producto o registro específico del carrito utilizando su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro del carrito encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro del carrito no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Carrito>> obtenerCarritoPorId(
        @Parameter(description = "ID del registro del carrito", example = "1")
        @PathVariable Long id) {

    Carrito carrito = carritoService.findById(id);

    if (carrito != null) {
        return ResponseEntity.ok(carritoModelAssembler.toModel(carrito));
    }

    throw new ResourceNotFoundException("Registro del carrito no encontrado con id: " + id);
    }

    @PostMapping
    @Operation(
            summary = "Agregar producto al carrito",
            description = "Registra un producto dentro del carrito de compras, indicando la información asociada al producto y la cantidad solicitada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto agregado al carrito correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Carrito> agregarProductoAlCarrito(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto o registro que se desea agregar al carrito",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Carrito.class),
                            examples = @ExampleObject(
                                    name = "Item de carrito valido",
                                    value = """
                                            {
                                              "usuario": {
                                                "id": 1
                                              },
                                              "producto": {
                                                "id": 1
                                              },
                                              "cantidad": 2
                                            }
                                            """
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody Carrito carrito) {

        Carrito carritoCreado = carritoService.save(carrito);
        URI location = carritoModelAssembler.toSelfUri(carritoCreado);

        return ResponseEntity.created(location).body(carritoCreado);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar registro del carrito",
            description = "Actualiza la información de un producto o registro existente dentro del carrito de compras."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro del carrito actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Registro del carrito no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Carrito> actualizarCarrito(
            @Parameter(description = "ID del registro del carrito a actualizar", example = "1")
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del registro del carrito",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Carrito.class),
                            examples = @ExampleObject(
                                    name = "Item de carrito actualizado",
                                    value = """
                                            {
                                              "usuario": {
                                                "id": 1
                                              },
                                              "producto": {
                                                "id": 1
                                              },
                                              "cantidad": 4
                                            }
                                            """
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody Carrito carrito) {

        Carrito existente = carritoService.findById(id);

        if (existente != null) {
            carrito.setId(id);
            return ResponseEntity.ok(carritoService.save(carrito));
        }

        throw new ResourceNotFoundException("Registro del carrito no encontrado con id: " + id);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar producto del carrito",
            description = "Elimina un producto o registro del carrito según su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro del carrito no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarProductoDelCarrito(
            @Parameter(description = "ID del registro del carrito a eliminar", example = "1")
            @PathVariable Long id) {

        Carrito carrito = carritoService.findById(id);

        if (carrito != null) {
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        throw new ResourceNotFoundException("Registro del carrito no encontrado con id: " + id);
    }
}
