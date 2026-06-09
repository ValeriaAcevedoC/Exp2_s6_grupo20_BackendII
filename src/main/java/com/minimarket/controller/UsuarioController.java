package com.minimarket.controller;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UsuarioDto> listarUsuarios() {
        return usuarioService.findAll().stream()
                .map(this::toUsuarioDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(ResponseEntity::ok) // Si el usuario existe, devuelve 200 OK con el usuario
            .orElseGet(() -> ResponseEntity.notFound().build()); // Si no, devuelve 404
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
