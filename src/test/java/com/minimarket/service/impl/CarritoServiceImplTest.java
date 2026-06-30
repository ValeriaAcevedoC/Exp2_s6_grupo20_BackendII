package com.minimarket.service.impl;

import com.minimarket.entity.Carrito;
import com.minimarket.repository.CarritoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    @Test
    void findAll_debeRetornarCarritos() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);

        when(carritoRepository.findAll()).thenReturn(List.of(carrito));

        List<Carrito> resultado = carritoService.findAll();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    void findById_cuandoExiste_debeRetornarCarrito() {
        Carrito carrito = new Carrito();
        carrito.setId(2L);

        when(carritoRepository.findById(2L)).thenReturn(Optional.of(carrito));

        Carrito resultado = carritoService.findById(2L);

        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
    }

    @Test
    void findById_cuandoNoExiste_debeRetornarNull() {
        when(carritoRepository.findById(99L)).thenReturn(Optional.empty());

        Carrito resultado = carritoService.findById(99L);

        assertNull(resultado);
    }

    @Test
    void save_debeGuardarCarrito() {
        Carrito carrito = new Carrito();
        carrito.setId(3L);

        when(carritoRepository.save(carrito)).thenReturn(carrito);

        Carrito resultado = carritoService.save(carrito);

        assertSame(carrito, resultado);
    }

    @Test
    void deleteById_debeEliminarCarrito() {
        carritoService.deleteById(4L);

        verify(carritoRepository).deleteById(4L);
    }

    @Test
    void findByUsuarioId_debeRetornarCarritosDelUsuario() {
        Carrito carrito = new Carrito();
        carrito.setId(5L);

        when(carritoRepository.findByUsuarioId(10L)).thenReturn(List.of(carrito));

        List<Carrito> resultado = carritoService.findByUsuarioId(10L);

        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getId());
    }
}
