package com.minimarket.controller;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

    @GetMapping
    public CollectionModel<EntityModel<UsuarioDto>> listarUsuarios() {
        List<EntityModel<UsuarioDto>> usuarios = usuarioService.findAll().stream()
                .map(usuario -> {
                UsuarioDto dto = toUsuarioDto(usuario);

                    return EntityModel.of(dto,
                        linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId())).withSelfRel(),
                        linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios")
                    );
                })
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
            linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioDto>> obtenerUsuarioPorId(@PathVariable Long id) {
    Optional<Usuario> usuario = usuarioService.findById(id);

        if (usuario.isPresent()) {
            UsuarioDto dto = toUsuarioDto(usuario.get());

            EntityModel<UsuarioDto> usuarioModel = EntityModel.of(dto,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(id)).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios")
            );

            return ResponseEntity.ok(usuarioModel);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Usuario guardarUsuario(@RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioService.save(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioService.findById(id);
        if (usuarioExistente.isPresent()) {
            usuario.setId(id);
            return ResponseEntity.ok(usuarioService.save(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) { // Verifica si el usuario existe
            usuarioService.deleteById(id); // Elimina al usuario
            return ResponseEntity.noContent().build(); // Respuesta 204 (sin contenido)
        }
        return ResponseEntity.notFound().build(); // Respuesta 404 (no encontrado)
    }

    private UsuarioDto toUsuarioDto(Usuario usuario) {
        List<Long> roleIds = usuario.getRoles() == null
                ? List.of()
                : usuario.getRoles().stream()
                        .map(Rol::getId)
                        .collect(Collectors.toList());

        return new UsuarioDto(usuario.getId(), usuario.getUsername(), roleIds);
    }

    public static class UsuarioDto {
        private Long id;
        private String username;
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
