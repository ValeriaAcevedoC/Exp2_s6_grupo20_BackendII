package com.minimarket.service.impl;

import com.minimarket.entity.Inventario;
import com.minimarket.repository.InventarioRepository;
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
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    @Test
    void findAll_debeRetornarInventarios() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);

        when(inventarioRepository.findAll())
                .thenReturn(List.of(inventario));

        List<Inventario> resultado = inventarioService.findAll();

        assertEquals(1, resultado.size());
    }

    @Test
    void findById_debeRetornarInventario() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);

        when(inventarioRepository.findById(1L))
                .thenReturn(Optional.of(inventario));

        Inventario resultado = inventarioService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void save_debeGuardarInventario() {
        Inventario inventario = new Inventario();
        inventario.setCantidad(10);
        inventario.setTipoMovimiento("Entrada");

        when(inventarioRepository.save(inventario))
                .thenReturn(inventario);

        Inventario resultado = inventarioService.save(inventario);

        assertEquals(10, resultado.getCantidad());
        assertEquals("Entrada", resultado.getTipoMovimiento());
    }

    @Test
    void deleteById_debeEliminarInventario() {
        inventarioService.deleteById(1L);

        verify(inventarioRepository).deleteById(1L);
    }

    @Test
    void findByProductoId_debeRetornarInventariosDelProducto() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);

        when(inventarioRepository.findByProductoId(5L))
                .thenReturn(List.of(inventario));

        List<Inventario> resultado = inventarioService.findByProductoId(5L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }
}
