package com.minimarket.controller;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.exception.ResourceNotFoundException;
import com.minimarket.hateoas.UsuarioModelAssembler;
import com.minimarket.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

@RestController
@RequestMapping("/api/usuarios")
@Tag(
    name = "Usuarios",
    description = "Endpoints para la gestión de usuarios del sistema Minimarket Plus"
)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioModelAssembler usuarioModelAssembler;

    @GetMapping
    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene todos los usuarios registrados en el sistema, exponiendo una vista sin password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public CollectionModel<EntityModel<UsuarioDto>> listarUsuarios() {
        List<UsuarioDto> usuarios = usuarioService.findAll().stream()
                .map(this::toUsuarioDto)
                .collect(Collectors.toList());

        return usuarioModelAssembler.toCollectionModel(usuarios);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Busca un usuario especifico utilizando su identificador unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<UsuarioDto>> obtenerUsuarioPorId(
            @Parameter(description = "ID del usuario a buscar", example = "1")
            @PathVariable Long id) {
    Optional<Usuario> usuario = usuarioService.findById(id);

        if (usuario.isPresent()) {
            UsuarioDto dto = toUsuarioDto(usuario.get());
            return ResponseEntity.ok(usuarioModelAssembler.toModel(dto));
        }

        throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
    }

    @PostMapping
    @Operation(
            summary = "Crear usuario",
            description = "Registra un nuevo usuario en el sistema y almacena su password encriptada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Usuario> guardarUsuario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del usuario que se desea registrar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class),
                            examples = @ExampleObject(
                                    name = "Usuario valido",
                                    value = """
                                            {
                                              "username": "admin",
                                              "password": "password123",
                                              "roles": [
                                                {
                                                  "id": 1
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
            @RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario usuarioCreado = usuarioService.save(usuario);
        URI location = usuarioModelAssembler.toSelfUri(toUsuarioDto(usuarioCreado));

        return ResponseEntity.created(location).body(usuarioCreado);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza la informacion de un usuario existente segun su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Usuario> actualizarUsuario(
            @Parameter(description = "ID del usuario a actualizar", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del usuario",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class),
                            examples = @ExampleObject(
                                    name = "Usuario actualizado",
                                    value = """
                                            {
                                              "username": "admin_actualizado",
                                              "password": "password123",
                                              "roles": [
                                                {
                                                  "id": 1
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
            @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioService.findById(id);
        if (usuarioExistente.isPresent()) {
            usuario.setId(id);
            return ResponseEntity.ok(usuarioService.save(usuario));
        }
        throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario del sistema utilizando su identificador unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarUsuario(
            @Parameter(description = "ID del usuario a eliminar", example = "1")
            @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) { // Verifica si el usuario existe
            usuarioService.deleteById(id); // Elimina al usuario
            return ResponseEntity.noContent().build(); // Respuesta 204 (sin contenido)
        }
        throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
    }

    private UsuarioDto toUsuarioDto(Usuario usuario) {
        List<Long> roleIds = usuario.getRoles() == null
                ? List.of()
                : usuario.getRoles().stream()
                        .map(Rol::getId)
                        .collect(Collectors.toList());

        return new UsuarioDto(usuario.getId(), usuario.getUsername(), roleIds);
    }

    @Schema(description = "Vista publica de usuario sin exponer password.")
    public static class UsuarioDto {
        @Schema(description = "Identificador unico del usuario.", example = "1")
        private Long id;

        @Schema(description = "Nombre de usuario.", example = "admin")
        private String username;

        @Schema(description = "IDs de roles asignados al usuario.", example = "[1, 2]")
        private List<Long> roles;

        public UsuarioDto(Long id, String username, List<Long> roles) {
            this.id = id;
            this.username = username;
            this.roles = roles;
        }

        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public List<Long> getRoles() {
            return roles;
        }
    }
}
