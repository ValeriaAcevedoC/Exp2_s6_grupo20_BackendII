package com.minimarket.service.impl;

import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
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
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void findAll_debeRetornarListaDeProductos() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Pan");

        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.findAll();

        assertEquals(1, resultado.size());
        assertEquals("Pan", resultado.get(0).getNombre());
    }

    @Test
    void findById_cuandoExiste_debeRetornarProducto() {
        Producto producto = new Producto();
        producto.setId(1L);

        when(productoRepository.findById(1L))
                .thenReturn(Optional.of(producto));

        Producto resultado = productoService.findById(1L);

        assertNotNull(resultado);
    }

    @Test
    void save_debeGuardarProducto() {
        Producto producto = new Producto();
        producto.setNombre("Leche");

        when(productoRepository.save(producto))
                .thenReturn(producto);

        Producto resultado = productoService.save(producto);

        assertEquals("Leche", resultado.getNombre());
    }

    @Test
    void deleteById_debeEliminarProducto() {
        productoService.deleteById(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    void findByCategoriaId_debeRetornarProductosDeCategoria() {
        Producto producto = new Producto();
        producto.setId(1L);

        when(productoRepository.findByCategoriaId(7L))
                .thenReturn(List.of(producto));

        List<Producto> resultado = productoService.findByCategoriaId(7L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }
}
