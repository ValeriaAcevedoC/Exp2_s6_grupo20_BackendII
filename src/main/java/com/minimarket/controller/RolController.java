package com.minimarket.controller;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Endpoints para la gestion de roles de usuario")
public class RolController {

    private final RolRepository rolRepository;

    public RolController(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @PostMapping
    @Operation(
            summary = "Crear rol",
            description = "Registra un nuevo rol de usuario en el sistema Minimarket Plus."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Rol> crearRol(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del rol que se desea registrar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Rol.class),
                            examples = @ExampleObject(
                                    name = "Rol valido",
                                    value = """
                                            {
                                              "nombre": "ADMIN"
                                            }
                                            """
                            )
                    )
            )
            @RequestBody Rol rol) {
        Rol rolCreado = rolRepository.save(rol);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(rolCreado.getId())
                .toUri();

        return ResponseEntity.created(location).body(rolCreado);
    }

    @GetMapping
    @Operation(
            summary = "Listar roles",
            description = "Obtiene todos los roles registrados en el sistema, incluyendo los IDs de usuarios asociados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de roles obtenida correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<RolDto> listarRoles() {
        return rolRepository.findAll().stream()
                .map(this::toRolDto)
                .collect(Collectors.toList());
    }

    private RolDto toRolDto(Rol rol) {
        List<Long> usuarioIds = rol.getUsuarios() == null
                ? Collections.emptyList()
                : rol.getUsuarios().stream()
                        .map(Usuario::getId)
                        .collect(Collectors.toList());

        return new RolDto(rol.getId(), rol.getNombre(), usuarioIds);
    }

    @Schema(description = "Vista de rol con los IDs de usuarios asociados.")
    public static class RolDto {
        @Schema(description = "Identificador unico del rol.", example = "1")
        private Long id;

        @Schema(description = "Nombre del rol.", example = "ADMIN")
        private String nombre;

        @Schema(description = "IDs de usuarios asociados al rol.", example = "[1, 2]")
        private List<Long> usuarios;

        public RolDto(Long id, String nombre, List<Long> usuarios) {
            this.id = id;
            this.nombre = nombre;
            this.usuarios = usuarios;
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public List<Long> getUsuarios() {
            return usuarios;
        }
    }
}
