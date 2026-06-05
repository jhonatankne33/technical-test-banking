package com.jvanegas.account_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EstadoCuentaResponse {
    
    private String cliente;
    private List<CuentaDetalle> cuentas;
    
    @Getter
    @Setter
    public static class CuentaDetalle {
        private String numeroCuenta;
        private String tipoCuenta;
        private BigDecimal saldoInicial;
        private Boolean estado;
        private BigDecimal saldoDisponible;
        private List<MovimientoDetalle> movimientos;
    }
    
    @Getter
    @Setter
    public static class MovimientoDetalle {
        private LocalDateTime fecha;
        private String tipoMovimiento;
        private BigDecimal valor;
        private BigDecimal saldoDisponible;
    }
}
