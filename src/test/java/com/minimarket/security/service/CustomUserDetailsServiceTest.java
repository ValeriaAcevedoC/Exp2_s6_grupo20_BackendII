package com.minimarket.security.service;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_cuandoExiste_debeRetornarCustomUserDetails() {
        Rol rol = new Rol();
        rol.setNombre("ROLE_ADMINISTRADOR");
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPassword("secret");
        usuario.setRoles(Set.of(rol));
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        UserDetails resultado = service.loadUserByUsername("admin");

        assertEquals("admin", resultado.getUsername());
        assertEquals("secret", resultado.getPassword());
    }

    @Test
    void loadUserByUsername_cuandoNoExiste_debeLanzarExcepcion() {
        when(usuarioRepository.findByUsername("desconocido")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("desconocido")
        );

        assertEquals("Usuario no encontrado: desconocido", exception.getMessage());
    }
}
