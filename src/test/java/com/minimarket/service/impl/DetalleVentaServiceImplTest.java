package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.repository.DetalleVentaRepository;
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
class DetalleVentaServiceImplTest {

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @InjectMocks
    private DetalleVentaServiceImpl detalleVentaService;

    @Test
    void findAll_debeRetornarDetallesVenta() {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(1L);

        when(detalleVentaRepository.findAll()).thenReturn(List.of(detalle));

        List<DetalleVenta> resultado = detalleVentaService.findAll();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    void findById_cuandoExiste_debeRetornarDetalleVenta() {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(2L);

        when(detalleVentaRepository.findById(2L)).thenReturn(Optional.of(detalle));

        DetalleVenta resultado = detalleVentaService.findById(2L);

        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
    }

    @Test
    void findById_cuandoNoExiste_debeRetornarNull() {
        when(detalleVentaRepository.findById(99L)).thenReturn(Optional.empty());

        DetalleVenta resultado = detalleVentaService.findById(99L);

        assertNull(resultado);
    }

    @Test
    void save_debeGuardarDetalleVenta() {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setCantidad(3);

        when(detalleVentaRepository.save(detalle)).thenReturn(detalle);

        DetalleVenta resultado = detalleVentaService.save(detalle);

        assertSame(detalle, resultado);
        assertEquals(3, resultado.getCantidad());
    }

    @Test
    void deleteById_debeEliminarDetalleVenta() {
        detalleVentaService.deleteById(4L);

        verify(detalleVentaRepository).deleteById(4L);
    }

    @Test
    void findByVentaId_debeRetornarDetallesPorVenta() {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(5L);

        when(detalleVentaRepository.findByVentaId(10L)).thenReturn(List.of(detalle));

        List<DetalleVenta> resultado = detalleVentaService.findByVentaId(10L);

        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getId());
    }
}
