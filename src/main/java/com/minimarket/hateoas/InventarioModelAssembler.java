package com.minimarket.hateoas;

import com.minimarket.controller.InventarioController;
import com.minimarket.controller.ProductoController;
import com.minimarket.entity.Inventario;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class InventarioModelAssembler {

    public EntityModel<Inventario> toModel(Inventario inventario) {
        List<Link> links = new ArrayList<>(List.of(
                linkTo(methodOn(InventarioController.class)
                        .obtenerMovimientoPorId(inventario.getId())).withSelfRel(),
                linkTo(methodOn(InventarioController.class)
                        .listarMovimientosDeInventario()).withRel("inventario"),
                linkTo(methodOn(InventarioController.class)
                        .actualizarMovimiento(inventario.getId(), inventario)).withRel("actualizar"),
                linkTo(methodOn(InventarioController.class)
                        .eliminarMovimiento(inventario.getId())).withRel("eliminar")
        ));

        if (inventario.getProducto() != null && inventario.getProducto().getId() != null) {
            links.add(linkTo(methodOn(ProductoController.class)
                    .obtenerProductoPorId(inventario.getProducto().getId())).withRel("producto"));
        }

        return EntityModel.of(inventario, links);
    }

    public CollectionModel<EntityModel<Inventario>> toCollectionModel(List<Inventario> inventarios) {
        List<EntityModel<Inventario>> inventarioModels = inventarios.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(inventarioModels,
                linkTo(methodOn(InventarioController.class)
                        .listarMovimientosDeInventario()).withSelfRel()
        );
    }

    public URI toSelfUri(Inventario inventario) {
        return linkTo(methodOn(InventarioController.class)
                .obtenerMovimientoPorId(inventario.getId())).toUri();
    }
}
