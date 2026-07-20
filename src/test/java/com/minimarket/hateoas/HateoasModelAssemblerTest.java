package com.minimarket.hateoas;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HateoasModelAssemblerTest {

    private final ProductoModelAssembler productoModelAssembler = new ProductoModelAssembler();
    private final CarritoModelAssembler carritoModelAssembler = new CarritoModelAssembler();
    private final InventarioModelAssembler inventarioModelAssembler = new InventarioModelAssembler();

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void productoToModel_conCategoriaSinId_noDebeAgregarLinkCategoria() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setCategoria(new Categoria());

        assertFalse(productoModelAssembler.toModel(producto).getLinks().hasLink("categoria"));
    }

    @Test
    void productoToModel_conCategoriaConId_debeAgregarLinkCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId(2L);
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setCategoria(categoria);

        assertTrue(productoModelAssembler.toModel(producto).getLinks().hasLink("categoria"));
    }

    @Test
    void carritoToModel_conRelacionesSinId_noDebeAgregarLinksRelacionales() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(new Usuario());
        carrito.setProducto(new Producto());

        var links = carritoModelAssembler.toModel(carrito).getLinks();

        assertFalse(links.hasLink("usuario"));
        assertFalse(links.hasLink("producto"));
    }

    @Test
    void carritoToModel_conRelacionesConId_debeAgregarLinksRelacionales() {
        Usuario usuario = new Usuario();
        usuario.setId(3L);
        Producto producto = new Producto();
        producto.setId(4L);
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);

        var links = carritoModelAssembler.toModel(carrito).getLinks();

        assertTrue(links.hasLink("usuario"));
        assertTrue(links.hasLink("producto"));
    }

    @Test
    void inventarioToModel_conProductoSinId_noDebeAgregarLinkProducto() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(new Producto());

        assertFalse(inventarioModelAssembler.toModel(inventario).getLinks().hasLink("producto"));
    }

    @Test
    void inventarioToModel_conProductoConId_debeAgregarLinkProducto() {
        Producto producto = new Producto();
        producto.setId(5L);
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(producto);

        assertTrue(inventarioModelAssembler.toModel(inventario).getLinks().hasLink("producto"));
    }
}
