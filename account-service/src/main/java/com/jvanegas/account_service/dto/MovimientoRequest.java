package com.jvanegas.account_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MovimientoRequest {
    
    private String numeroCuenta;
    private String tipoMovimiento;
    private BigDecimal valor;
}
