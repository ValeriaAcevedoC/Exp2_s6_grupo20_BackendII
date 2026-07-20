package com.minimarket.controller;

import com.minimarket.entity.Producto;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.hateoas.ProductoModelAssembler;
import com.minimarket.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Endpoints para la gestión de productos del sistema Minimarket Plus")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoModelAssembler productoModelAssembler;

    @GetMapping
    @Operation(
            summary = "Listar productos",
            description = "Obtiene la lista completa de productos registrados en el sistema Minimarket Plus."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public CollectionModel<EntityModel<Producto>> listarProductos() {
        return productoModelAssembler.toCollectionModel(productoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener producto por ID",
            description = "Busca un producto específico utilizando su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Producto>> obtenerProductoPorId(
            @Parameter(description = "ID del producto a buscar", example = "1")
            @PathVariable Long id) {

        Producto producto = productoService.findById(id);

        if (producto != null) {
            return ResponseEntity.ok(productoModelAssembler.toModel(producto));
        }

        throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
    }

    @PostMapping
    @Operation(
            summary = "Crear producto",
            description = "Registra un nuevo producto en el sistema Minimarket Plus."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Producto> guardarProducto(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto que se desea registrar en el sistema",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Producto.class),
                            examples = @ExampleObject(
                                    name = "Producto valido",
                                    value = """
                                            {
                                              "nombre": "Pan integral",
                                              "precio": 1590.0,
                                              "stock": 25,
                                              "categoria": {
                                                "id": 1
                                              }
                                            }
                                            """
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody Producto producto) {

        Producto productoCreado = productoService.save(producto);
        URI location = productoModelAssembler.toSelfUri(productoCreado);

        return ResponseEntity.created(location).body(productoCreado);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza la información de un producto existente según su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Producto> actualizarProducto(
            @Parameter(description = "ID del producto a actualizar", example = "1")
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del producto",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Producto.class),
                            examples = @ExampleObject(
                                    name = "Producto actualizado",
                                    value = """
                                            {
                                              "nombre": "Pan integral familiar",
                                              "precio": 1890.0,
                                              "stock": 40,
                                              "categoria": {
                                                "id": 1
                                              }
                                            }
                                            """
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody Producto producto) {

        Producto productoExistente = productoService.findById(id);

        if (productoExistente != null) {
            producto.setId(id);
            return ResponseEntity.ok(productoService.save(producto));
        }

        throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar producto",
            description = "Elimina un producto del sistema utilizando su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "ID del producto a eliminar", example = "1")
            @PathVariable Long id) {

        Producto producto = productoService.findById(id);

        if (producto != null) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
    }
}
