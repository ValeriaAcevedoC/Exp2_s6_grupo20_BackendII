package com.minimarket.service.impl;

import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void findAll_debeRetornarUsuarios() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.findAll();

        assertEquals(1, resultado.size());
        assertEquals("admin", resultado.get(0).getUsername());
    }

    @Test
    void findById_debeRetornarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findById(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    void findByUsername_debeRetornarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setUsername("cajero");

        when(usuarioRepository.findByUsername("cajero"))
                .thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado =
                usuarioService.findByUsername("cajero");

        assertTrue(resultado.isPresent());
        assertEquals("cajero", resultado.get().getUsername());
    }

    @Test
    void save_debeGuardarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setUsername("cliente");

        when(usuarioRepository.save(usuario))
                .thenReturn(usuario);

        Usuario resultado = usuarioService.save(usuario);

        assertEquals("cliente", resultado.getUsername());
    }

    @Test
    void deleteById_debeEliminarUsuario() {
        usuarioService.deleteById(1L);

        verify(usuarioRepository).deleteById(1L);
    }
}