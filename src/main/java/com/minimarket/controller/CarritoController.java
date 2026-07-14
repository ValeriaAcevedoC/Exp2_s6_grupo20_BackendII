package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Endpoints para la gestión del carrito de compras del sistema Minimarket Plus")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

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
    List<EntityModel<Carrito>> carritos = carritoService.findAll().stream()
            .map(carrito -> EntityModel.of(carrito,
                    linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(carrito.getId())).withSelfRel(),
                    linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("carrito")
            ))
            .toList();

            return CollectionModel.of(carritos,
            linkTo(methodOn(CarritoController.class).listarCarrito()).withSelfRel()
             );
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
        EntityModel<Carrito> carritoModel = EntityModel.of(carrito,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(id)).withSelfRel(),
                linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("carrito")
        );

        return ResponseEntity.ok(carritoModel);
    }

    return ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(
            summary = "Agregar producto al carrito",
            description = "Registra un producto dentro del carrito de compras, indicando la información asociada al producto y la cantidad solicitada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto agregado al carrito correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Carrito agregarProductoAlCarrito(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto o registro que se desea agregar al carrito"
            )
            @org.springframework.web.bind.annotation.RequestBody Carrito carrito) {

        return carritoService.save(carrito);
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
                    description = "Datos actualizados del registro del carrito"
            )
            @org.springframework.web.bind.annotation.RequestBody Carrito carrito) {

        Carrito existente = carritoService.findById(id);

        if (existente != null) {
            carrito.setId(id);
            return ResponseEntity.ok(carritoService.save(carrito));
        }

        return ResponseEntity.notFound().build();
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

        return ResponseEntity.notFound().build();
    }
}