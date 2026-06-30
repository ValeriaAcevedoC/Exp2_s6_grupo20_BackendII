package com.minimarket.service.impl;

import com.minimarket.entity.Venta;
import com.minimarket.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    @Test
    void findAll_debeRetornarVentas() {
        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaRepository.findAll()).thenReturn(List.of(venta));

        List<Venta> resultado = ventaService.findAll();

        assertEquals(1, resultado.size());
    }

    @Test
    void findById_debeRetornarVenta() {
        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaRepository.findById(1L))
                .thenReturn(Optional.of(venta));

        Venta resultado = ventaService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void save_debeGuardarVenta() {
        Venta venta = new Venta();
        venta.setFecha(new Date());

        when(ventaRepository.save(venta)).thenReturn(venta);

        Venta resultado = ventaService.save(venta);

        assertNotNull(resultado);
        assertNotNull(resultado.getFecha());
    }

    @Test
    void findByUsuarioId_debeRetornarVentasDelUsuario() {
        Venta venta = new Venta();
        venta.setId(1L);

        when(ventaRepository.findByUsuarioId(2L))
                .thenReturn(List.of(venta));

        List<Venta> resultado = ventaService.findByUsuarioId(2L);

        assertEquals(1, resultado.size());
    }
}