package com.minimarket.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void run_cuandoNoExistenRolNiUsuario_debeCrearlos() {
        Rol rolGuardado = new Rol();
        rolGuardado.setId(1L);
        rolGuardado.setNombre("ROLE_ADMINISTRADOR");
        when(rolRepository.findByNombre("ROLE_ADMINISTRADOR")).thenReturn(Optional.empty());
        when(rolRepository.save(any(Rol.class))).thenReturn(rolGuardado);
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin123")).thenReturn("encoded");
        DataInitializer initializer = new DataInitializer(usuarioRepository, rolRepository, passwordEncoder);

        initializer.run();

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario usuario = usuarioCaptor.getValue();
        assertEquals("admin", usuario.getUsername());
        assertEquals("encoded", usuario.getPassword());
        assertTrue(usuario.getRoles().contains(rolGuardado));
    }

    @Test
    void run_cuandoUsuarioExiste_noDebeCrearloOtraVez() {
        Rol rolExistente = new Rol();
        rolExistente.setNombre("ROLE_ADMINISTRADOR");
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setUsername("admin");
        when(rolRepository.findByNombre("ROLE_ADMINISTRADOR")).thenReturn(Optional.of(rolExistente));
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioExistente));
        DataInitializer initializer = new DataInitializer(usuarioRepository, rolRepository, passwordEncoder);

        initializer.run();

        verify(rolRepository, never()).save(any(Rol.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(any());
    }
}
