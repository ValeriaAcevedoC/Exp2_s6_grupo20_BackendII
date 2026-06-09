package com.minimarket.controller;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolRepository rolRepository;

    public RolController(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @PostMapping
    public Rol crearRol(@RequestBody Rol rol) {
        return rolRepository.save(rol);
    }

    @GetMapping
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

    public static class RolDto {
        private Long id;
        private String nombre;
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