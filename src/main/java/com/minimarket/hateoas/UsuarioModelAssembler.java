package com.minimarket.hateoas;

import com.minimarket.controller.RolController;
import com.minimarket.controller.UsuarioController;
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
public class UsuarioModelAssembler {

    public EntityModel<UsuarioController.UsuarioDto> toModel(UsuarioController.UsuarioDto usuario) {
        List<Link> links = new ArrayList<>(List.of(
                linkTo(methodOn(UsuarioController.class)
                        .obtenerUsuarioPorId(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class)
                        .listarUsuarios()).withRel("usuarios"),
                linkTo(methodOn(UsuarioController.class)
                        .actualizarUsuario(usuario.getId(), null)).withRel("actualizar"),
                linkTo(methodOn(UsuarioController.class)
                        .eliminarUsuario(usuario.getId())).withRel("eliminar"),
                linkTo(methodOn(RolController.class)
                        .listarRoles()).withRel("roles")
        ));

        return EntityModel.of(usuario, links);
    }

    public CollectionModel<EntityModel<UsuarioController.UsuarioDto>> toCollectionModel(
            List<UsuarioController.UsuarioDto> usuarios) {

        List<EntityModel<UsuarioController.UsuarioDto>> usuarioModels = usuarios.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(usuarioModels,
                linkTo(methodOn(UsuarioController.class)
                        .listarUsuarios()).withSelfRel()
        );
    }

    public URI toSelfUri(UsuarioController.UsuarioDto usuario) {
        return linkTo(methodOn(UsuarioController.class)
                .obtenerUsuarioPorId(usuario.getId())).toUri();
    }
}
