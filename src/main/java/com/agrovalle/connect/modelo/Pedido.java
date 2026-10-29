package com.agrovalle.connect.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Pedido iniciado por un comprador. Cada pedido pertenece a una sola cuenta. */
@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String referencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comprador_id", nullable = false)
    private Comprador comprador;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false, length = 4000)
    private String detalle;

    protected Pedido() { }

    public Pedido(String referencia, Comprador comprador, BigDecimal total, String estado,
            LocalDateTime fechaCreacion, String detalle) {
        this.referencia = referencia;
        this.comprador = comprador;
        this.total = total;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.detalle = detalle;
    }

    public Long getId() { return id; }
    public String getReferencia() { return referencia; }
    public Comprador getComprador() { return comprador; }
    public BigDecimal getTotal() { return total; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public String getDetalle() { return detalle; }
}
