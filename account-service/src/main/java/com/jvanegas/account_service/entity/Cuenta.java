package com.jvanegas.account_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "cuentas")
public class Cuenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String numeroCuenta;
    
    @Column(nullable = false)
    private String tipoCuenta;
    
    @Column(nullable = false)
    private BigDecimal saldoInicial;
    
    @Column(nullable = false)
    private Boolean estado;
    
    @Column(nullable = false)
    private Long clienteId;
}
