package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public CollectionModel<EntityModel<Inventario>> listarMovimientosDeInventario() {

        List<EntityModel<Inventario>> inventarios = inventarioService.findAll().stream()
                .map(inventario -> EntityModel.of(inventario,
                        linkTo(methodOn(InventarioController.class)
                            .obtenerMovimientoPorId(inventario.getId())).withSelfRel(),
                        linkTo(methodOn(InventarioController.class)
                            .listarMovimientosDeInventario()).withRel("inventario")
                ))
                .toList();

        return CollectionModel.of(inventarios,
            linkTo(methodOn(InventarioController.class)
                    .listarMovimientosDeInventario()).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> obtenerMovimientoPorId(@PathVariable Long id) {

        Inventario inventario = inventarioService.findById(id);

        if (inventario != null) {

            EntityModel<Inventario> inventarioModel = EntityModel.of(inventario,
                linkTo(methodOn(InventarioController.class)
                        .obtenerMovimientoPorId(id)).withSelfRel(),
                linkTo(methodOn(InventarioController.class)
                        .listarMovimientosDeInventario()).withRel("inventario")
            );

            return ResponseEntity.ok(inventarioModel);
        }

        return ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public Inventario registrarMovimiento(@RequestBody Inventario inventario) {
        return inventarioService.save(inventario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizarMovimiento(@PathVariable Long id, @RequestBody Inventario inventario) {
        Inventario existente = inventarioService.findById(id);
        if (existente != null) {
            inventario.setId(id);
            return ResponseEntity.ok(inventarioService.save(inventario));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario != null) {
            inventarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
