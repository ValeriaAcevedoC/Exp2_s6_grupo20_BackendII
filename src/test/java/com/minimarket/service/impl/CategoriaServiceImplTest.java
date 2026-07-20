package com.minimarket.service.impl;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.repository.CategoriaRepository;
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
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Test
    void findAll_debeRetornarCategorias() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Lácteos");

        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> resultado = categoriaService.findAll();

        assertEquals(1, resultado.size());
        assertEquals("Lácteos", resultado.get(0).getNombre());
    }

    @Test
    void findById_cuandoExiste_debeRetornarCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId(2L);

        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoria));

        Categoria resultado = categoriaService.findById(2L);

        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
    }

    @Test
    void findById_cuandoNoExiste_debeRetornarNull() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        Categoria resultado = categoriaService.findById(99L);

        assertNull(resultado);
    }

    @Test
    void save_debeGuardarCategoria() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Bebidas");

        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria resultado = categoriaService.save(categoria);

        assertSame(categoria, resultado);
    }

    @Test
    void deleteById_debeEliminarCategoria() {
        categoriaService.deleteById(3L);

        verify(categoriaRepository).deleteById(3L);
    }

    @Test
    void categoria_debePermitirAsignarProductos() {
        Categoria categoria = new Categoria();
        Producto producto = new Producto();

        categoria.setProductos(List.of(producto));

        assertEquals(1, categoria.getProductos().size());
        assertSame(producto, categoria.getProductos().get(0));
    }
}
