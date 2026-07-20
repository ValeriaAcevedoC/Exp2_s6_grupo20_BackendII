package com.minimarket.hateoas;

import com.minimarket.controller.CarritoController;
import com.minimarket.controller.ProductoController;
import com.minimarket.controller.UsuarioController;
import com.minimarket.entity.Carrito;
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
public class CarritoModelAssembler {

    public EntityModel<Carrito> toModel(Carrito carrito) {
        List<Link> links = new ArrayList<>(List.of(
                linkTo(methodOn(CarritoController.class)
                        .obtenerCarritoPorId(carrito.getId())).withSelfRel(),
                linkTo(methodOn(CarritoController.class)
                        .listarCarrito()).withRel("carrito"),
                linkTo(methodOn(CarritoController.class)
                        .actualizarCarrito(carrito.getId(), carrito)).withRel("actualizar"),
                linkTo(methodOn(CarritoController.class)
                        .eliminarProductoDelCarrito(carrito.getId())).withRel("eliminar")
        ));

        if (carrito.getUsuario() != null && carrito.getUsuario().getId() != null) {
            links.add(linkTo(methodOn(UsuarioController.class)
                    .obtenerUsuarioPorId(carrito.getUsuario().getId())).withRel("usuario"));
        }

        if (carrito.getProducto() != null && carrito.getProducto().getId() != null) {
            links.add(linkTo(methodOn(ProductoController.class)
                    .obtenerProductoPorId(carrito.getProducto().getId())).withRel("producto"));
        }

        return EntityModel.of(carrito, links);
    }

    public CollectionModel<EntityModel<Carrito>> toCollectionModel(List<Carrito> carritos) {
        List<EntityModel<Carrito>> carritoModels = carritos.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(carritoModels,
                linkTo(methodOn(CarritoController.class)
                        .listarCarrito()).withSelfRel()
        );
    }

    public URI toSelfUri(Carrito carrito) {
        return linkTo(methodOn(CarritoController.class)
                .obtenerCarritoPorId(carrito.getId())).toUri();
    }
}
