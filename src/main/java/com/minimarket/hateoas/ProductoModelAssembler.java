package com.minimarket.hateoas;

import com.minimarket.controller.CategoriaController;
import com.minimarket.controller.ProductoController;
import com.minimarket.entity.Producto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductoModelAssembler {

    public EntityModel<Producto> toModel(Producto producto) {
        List<Link> links = new ArrayList<>(List.of(
                linkTo(methodOn(ProductoController.class)
                        .obtenerProductoPorId(producto.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class)
                        .listarProductos()).withRel("productos"),
                linkTo(methodOn(ProductoController.class)
                        .actualizarProducto(producto.getId(), producto)).withRel("actualizar"),
                linkTo(methodOn(ProductoController.class)
                        .eliminarProducto(producto.getId())).withRel("eliminar")
        ));

        if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
            links.add(linkTo(methodOn(CategoriaController.class)
                    .obtenerCategoriaPorId(producto.getCategoria().getId())).withRel("categoria"));
        }

        return EntityModel.of(producto, links);
    }

    public CollectionModel<EntityModel<Producto>> toCollectionModel(List<Producto> productos) {
        List<EntityModel<Producto>> productoModels = productos.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(productoModels,
                linkTo(methodOn(ProductoController.class)
                        .listarProductos()).withSelfRel()
        );
    }

    public URI toSelfUri(Producto producto) {
        return linkTo(methodOn(ProductoController.class)
                .obtenerProductoPorId(producto.getId())).toUri();
    }
}
