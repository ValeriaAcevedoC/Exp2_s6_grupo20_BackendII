package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.*;
import com.minimarket.exception.GlobalExceptionHandler;
import com.minimarket.hateoas.CarritoModelAssembler;
import com.minimarket.hateoas.InventarioModelAssembler;
import com.minimarket.hateoas.ProductoModelAssembler;
import com.minimarket.hateoas.UsuarioModelAssembler;
import com.minimarket.repository.RolRepository;
import com.minimarket.security.model.LoginRequest;
import com.minimarket.security.util.JwtUtil;
import com.minimarket.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ControllerCoverageStandaloneTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private ProductoService productoService;
    @Mock private UsuarioService usuarioService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private CarritoService carritoService;
    @Mock private CategoriaService categoriaService;
    @Mock private DetalleVentaService detalleVentaService;
    @Mock private InventarioService inventarioService;
    @Mock private VentaService ventaService;
    @Mock private RolRepository rolRepository;

    @Spy private ProductoModelAssembler productoModelAssembler = new ProductoModelAssembler();
    @Spy private UsuarioModelAssembler usuarioModelAssembler = new UsuarioModelAssembler();
    @Spy private CarritoModelAssembler carritoModelAssembler = new CarritoModelAssembler();
    @Spy private InventarioModelAssembler inventarioModelAssembler = new InventarioModelAssembler();

    @InjectMocks private AuthController authController;
    @InjectMocks private ProductoController productoController;
    @InjectMocks private UsuarioController usuarioController;
    @InjectMocks private CarritoController carritoController;
    @InjectMocks private CategoriaController categoriaController;
    @InjectMocks private DetalleVentaController detalleVentaController;
    @InjectMocks private InventarioController inventarioController;
    @InjectMocks private VentaController ventaController;
    @InjectMocks private RolController rolController;
    @InjectMocks private HolaMundoController holaMundoController;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(
                authController,
                productoController,
                usuarioController,
                carritoController,
                categoriaController,
                detalleVentaController,
                inventarioController,
                ventaController,
                rolController,
                holaMundoController
        ).setControllerAdvice(new GlobalExceptionHandler()).setValidator(validator).build();
    }

    @Test
    void authLogin_debeRetornarToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("password123");
        UserDetails userDetails = mock(UserDetails.class);
        when(authenticationManager.authenticate(any())).thenReturn(new TestingAuthenticationToken(userDetails, null));
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void authLogin_conDatosInvalidos_debeRetornarErrorNormalizado() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/auth/login"));
    }

    @Test
    void authLogin_conJsonInvalido_debeRetornarErrorNormalizado() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El cuerpo de la solicitud no es valido."));
    }

    @Test
    void authLogin_conCredencialesInvalidas_debeRetornarUnauthorizedNormalizado() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("password123");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Credenciales invalidas."));
    }

    @Test
    void errorInesperado_debeRetornarErrorNormalizado() throws Exception {
        when(ventaService.findAll()).thenThrow(new RuntimeException("Falla inesperada"));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Error interno del servidor."))
                .andExpect(jsonPath("$.path").value("/api/ventas"));
    }

    @Test
    void productoEndpoints_debenCubrirCrud() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Pan");
        when(productoService.findAll()).thenReturn(List.of(producto));
        when(productoService.findById(1L)).thenReturn(producto);
        when(productoService.findById(99L)).thenReturn(null);
        when(productoService.save(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Pan"))
                .andExpect(jsonPath("$.content[0].links").isArray())
                .andExpect(jsonPath("$.links").isArray());
        mockMvc.perform(get("/api/productos/1")).andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Pan"));
        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Producto no encontrado con id: 99"))
                .andExpect(jsonPath("$.path").value("/api/productos/99"));
        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(producto))).andExpect(status().isCreated()).andExpect(header().exists("Location")).andExpect(jsonPath("$.id").value(1));
        mockMvc.perform(put("/api/productos/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(producto))).andExpect(status().isOk());
        mockMvc.perform(put("/api/productos/99").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(producto))).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/productos/1")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/productos/99")).andExpect(status().isNotFound());
    }

    @Test
    void usuarioEndpoints_debenCubrirCrudYDto() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");
        usuario.setPassword("secret");
        Rol rol = new Rol();
        rol.setId(10L);
        usuario.setRoles(Set.of(rol));
        when(usuarioService.findAll()).thenReturn(List.of(usuario));
        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioService.findById(99L)).thenReturn(Optional.empty());
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("admin"))
                .andExpect(jsonPath("$.content[0].links").isArray())
                .andExpect(jsonPath("$.links").isArray());
        mockMvc.perform(get("/api/usuarios/1")).andExpect(status().isOk()).andExpect(jsonPath("$.username").value("admin"));
        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuario no encontrado con id: 99"));
        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(usuario))).andExpect(status().isCreated()).andExpect(header().exists("Location")).andExpect(jsonPath("$.username").value("admin"));
        mockMvc.perform(put("/api/usuarios/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(usuario))).andExpect(status().isOk());
        mockMvc.perform(put("/api/usuarios/99").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(usuario))).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/usuarios/1")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/usuarios/99")).andExpect(status().isNotFound());
    }

    @Test
    void usuarioListado_conRolesNulos_debeRetornarRolesVacios() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setUsername("sinroles");
        usuario.setRoles(null);
        when(usuarioService.findAll()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].roles").isArray())
                .andExpect(jsonPath("$.content[0].roles").isEmpty())
                .andExpect(jsonPath("$.content[0].links").isArray())
                .andExpect(jsonPath("$.links").isArray());
    }

    @Test
    void carritoEndpoints_debenCubrirCrud() throws Exception {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        when(carritoService.findAll()).thenReturn(List.of(carrito));
        when(carritoService.findById(1L)).thenReturn(carrito);
        when(carritoService.findById(99L)).thenReturn(null);
        when(carritoService.save(any(Carrito.class))).thenReturn(carrito);

        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].links").isArray())
                .andExpect(jsonPath("$.links").isArray());
        mockMvc.perform(get("/api/carrito/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/carrito/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/carrito").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(carrito))).andExpect(status().isCreated()).andExpect(header().exists("Location"));
        mockMvc.perform(put("/api/carrito/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(carrito))).andExpect(status().isOk());
        mockMvc.perform(put("/api/carrito/99").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(carrito))).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/carrito/1")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/carrito/99")).andExpect(status().isNotFound());
    }

    @Test
    void categoriaEndpoints_debenCubrirCrud() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Lácteos");
        when(categoriaService.findAll()).thenReturn(List.of(categoria));
        when(categoriaService.findById(1L)).thenReturn(categoria);
        when(categoriaService.findById(99L)).thenReturn(null);
        when(categoriaService.save(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(get("/api/categorias")).andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value("Lácteos"));
        mockMvc.perform(get("/api/categorias/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/categorias/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/categorias").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(categoria))).andExpect(status().isCreated()).andExpect(header().exists("Location"));
        mockMvc.perform(put("/api/categorias/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(categoria))).andExpect(status().isOk());
        mockMvc.perform(put("/api/categorias/99").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(categoria))).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/categorias/1")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/categorias/99")).andExpect(status().isNotFound());
    }

    @Test
    void detalleVentaEndpoints_debenCubrirCrud() throws Exception {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(1L);
        detalle.setCantidad(2);
        when(detalleVentaService.findAll()).thenReturn(List.of(detalle));
        when(detalleVentaService.findById(1L)).thenReturn(detalle);
        when(detalleVentaService.findById(99L)).thenReturn(null);
        when(detalleVentaService.save(any(DetalleVenta.class))).thenReturn(detalle);

        mockMvc.perform(get("/api/detalle-ventas")).andExpect(status().isOk()).andExpect(jsonPath("$[0].cantidad").value(2));
        mockMvc.perform(get("/api/detalle-ventas/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/detalle-ventas/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/detalle-ventas").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(detalle))).andExpect(status().isCreated()).andExpect(header().exists("Location"));
        mockMvc.perform(put("/api/detalle-ventas/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(detalle))).andExpect(status().isOk());
        mockMvc.perform(put("/api/detalle-ventas/99").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(detalle))).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/detalle-ventas/1")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/detalle-ventas/99")).andExpect(status().isNotFound());
    }

    @Test
    void inventarioEndpoints_debenCubrirCrud() throws Exception {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setCantidad(10);
        when(inventarioService.findAll()).thenReturn(List.of(inventario));
        when(inventarioService.findById(1L)).thenReturn(inventario);
        when(inventarioService.findById(99L)).thenReturn(null);
        when(inventarioService.save(any(Inventario.class))).thenReturn(inventario);

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cantidad").value(10))
                .andExpect(jsonPath("$.content[0].links").isArray())
                .andExpect(jsonPath("$.links").isArray());
        mockMvc.perform(get("/api/inventario/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/inventario/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/inventario").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(inventario))).andExpect(status().isCreated()).andExpect(header().exists("Location"));
        mockMvc.perform(put("/api/inventario/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(inventario))).andExpect(status().isOk());
        mockMvc.perform(put("/api/inventario/99").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(inventario))).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/inventario/1")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/inventario/99")).andExpect(status().isNotFound());
    }

    @Test
    void ventaEndpoints_debenCubrirCrud() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        when(ventaService.findAll()).thenReturn(List.of(venta));
        when(ventaService.findById(1L)).thenReturn(venta);
        when(ventaService.findById(99L)).thenReturn(null);
        when(ventaService.save(any(Venta.class))).thenReturn(venta);

        mockMvc.perform(get("/api/ventas")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1));
        mockMvc.perform(get("/api/ventas/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/ventas/99")).andExpect(status().isNotFound());
        mockMvc.perform(post("/api/ventas").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(venta))).andExpect(status().isCreated()).andExpect(header().exists("Location"));
    }

    @Test
    void rolEndpoints_debenCubrirCrearYListar() throws Exception {
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMIN");
        when(rolRepository.findAll()).thenReturn(List.of(rol));
        when(rolRepository.save(any(Rol.class))).thenReturn(rol);

        mockMvc.perform(get("/api/roles")).andExpect(status().isOk()).andExpect(jsonPath("$[0].nombre").value("ADMIN"));
        mockMvc.perform(post("/api/roles").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(rol))).andExpect(status().isCreated()).andExpect(header().exists("Location")).andExpect(jsonPath("$.nombre").value("ADMIN"));
    }

    @Test
    void rolListado_conUsuarios_debeRetornarIdsDeUsuarios() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(10L);
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMIN");
        rol.setUsuarios(Set.of(usuario));
        when(rolRepository.findAll()).thenReturn(List.of(rol));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarios[0]").value(10));
    }

    @Test
    void holaMundoEndpoint_debeRetornarMensaje() throws Exception {
        mockMvc.perform(get("/public/hola")).andExpect(status().isOk()).andExpect(content().string("¡Hola Mundo!"));
    }
}
