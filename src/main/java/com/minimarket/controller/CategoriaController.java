package com.minimarket.controller;

import com.minimarket.entity.Categoria;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.service.CategoriaService;
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
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Endpoints para la gestion de categorias de productos")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    @Operation(
            summary = "Listar categorias",
            description = "Obtiene todas las categorias registradas en el sistema Minimarket Plus."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorias obtenida correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<Categoria> listarCategorias() {
        return categoriaService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener categoria por ID",
            description = "Busca una categoria especifica utilizando su identificador unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada correctamente"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Categoria> obtenerCategoriaPorId(
            @Parameter(description = "ID de la categoria a buscar", example = "1")
            @PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria != null) {
            return ResponseEntity.ok(categoria);
        }

        throw new ResourceNotFoundException("Categoria no encontrada con id: " + id);
    }

    @PostMapping
    @Operation(
            summary = "Crear categoria",
            description = "Registra una nueva categoria de productos en el sistema Minimarket Plus."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Categoria> guardarCategoria(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la categoria que se desea registrar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Categoria.class),
                            examples = @ExampleObject(
                                    name = "Categoria valida",
                                    value = """
                                            {
                                              "nombre": "Lacteos"
                                            }
                                            """
                            )
                    )
            )
            @RequestBody Categoria categoria) {
        Categoria categoriaCreada = categoriaService.save(categoria);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoriaCreada.getId())
                .toUri();

        return ResponseEntity.created(location).body(categoriaCreada);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar categoria",
            description = "Actualiza la informacion de una categoria existente segun su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Categoria> actualizarCategoria(
            @Parameter(description = "ID de la categoria a actualizar", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados de la categoria",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Categoria.class),
                            examples = @ExampleObject(
                                    name = "Categoria actualizada",
                                    value = """
                                            {
                                              "nombre": "Bebidas"
                                            }
                                            """
                            )
                    )
            )
            @RequestBody Categoria categoria) {
        Categoria categoriaExistente = categoriaService.findById(id);
        if (categoriaExistente != null) {
            categoria.setId(id);
            return ResponseEntity.ok(categoriaService.save(categoria));
        }
        throw new ResourceNotFoundException("Categoria no encontrada con id: " + id);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar categoria",
            description = "Elimina una categoria del sistema utilizando su identificador unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarCategoria(
            @Parameter(description = "ID de la categoria a eliminar", example = "1")
            @PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria != null) {
            categoriaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("Categoria no encontrada con id: " + id);
    }
}
