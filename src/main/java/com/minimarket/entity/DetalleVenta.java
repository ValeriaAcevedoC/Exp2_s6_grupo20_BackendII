package com.minimarket.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Entity
@Schema(description = "Representa una linea o detalle de una venta realizada.")
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador unico del detalle de venta.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    @Schema(description = "Venta a la que pertenece este detalle.", implementation = Venta.class)
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    @Schema(description = "Producto vendido en este detalle.", implementation = Producto.class)
    private Producto producto;

    @Column(nullable = false)
    @Schema(description = "Cantidad de unidades vendidas.", example = "2")
    private Integer cantidad;

    @Column(nullable = false)
    @Schema(description = "Precio unitario aplicado al producto en la venta.", example = "1590.0")
    private Double precio;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
