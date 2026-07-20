package com.minimarket.entity;

import jakarta.persistence.*;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Schema(description = "Representa un rol de autorizacion asignable a usuarios.")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador unico del rol.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre del rol.", example = "ADMIN")
    private String nombre;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    @Schema(description = "Usuarios que tienen asignado este rol.", accessMode = Schema.AccessMode.READ_ONLY)
    private Set<Usuario> usuarios;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<Usuario> usuarios) {
        this.usuarios = usuarios;
    }
}
