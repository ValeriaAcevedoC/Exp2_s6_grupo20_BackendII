package com.minimarket.security.model;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomUserDetailsTest {

    @Test
    void getters_debenDelegarEnUsuario() {
        Rol rol = new Rol();
        rol.setNombre("ROLE_ADMINISTRADOR");
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPassword("secret");
        usuario.setRoles(Set.of(rol));

        CustomUserDetails userDetails = new CustomUserDetails(usuario);

        assertEquals("admin", userDetails.getUsername());
        assertEquals("secret", userDetails.getPassword());
        assertEquals("ROLE_ADMINISTRADOR", userDetails.getAuthorities().iterator().next().getAuthority());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }
}
