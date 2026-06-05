package com.jvanegas.account_service.service;

import com.jvanegas.account_service.dto.MovimientoRequest;
import com.jvanegas.account_service.dto.MovimientoResponse;
import com.jvanegas.account_service.entity.Cuenta;
import com.jvanegas.account_service.entity.Movimiento;
import com.jvanegas.account_service.exception.BusinessException;
import com.jvanegas.account_service.repository.CuentaRepository;
import com.jvanegas.account_service.repository.MovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {
    
    @Mock
    private MovimientoRepository movimientoRepository;
    
    @Mock
    private CuentaRepository cuentaRepository;
    
    @InjectMocks
    private MovimientoService movimientoService;
    
    private Cuenta cuenta;
    private MovimientoRequest movimientoRequest;
    
    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("478758");
        cuenta.setTipoCuenta("Ahorros");
        cuenta.setSaldoInicial(new BigDecimal("2000"));
        cuenta.setEstado(true);
        cuenta.setClienteId(1L);
        
        movimientoRequest = new MovimientoRequest();
        movimientoRequest.setNumeroCuenta("478758");
        movimientoRequest.setTipoMovimiento("Retiro");
        movimientoRequest.setValor(new BigDecimal("-575"));
    }
    
    @Test
    void registrarMovimiento_DeberiaRegistrarExitosamente() {
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findFirstByCuentaIdOrderByIdDesc(1L)).thenReturn(Optional.empty());
        
        Movimiento movimiento = new Movimiento();
        movimiento.setId(1L);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento("Retiro");
        movimiento.setValor(new BigDecimal("-575"));
        movimiento.setSaldo(new BigDecimal("1425"));
        movimiento.setCuenta(cuenta);
        
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);
        
        MovimientoResponse response = movimientoService.registrarMovimiento(movimientoRequest);
        
        assertNotNull(response);
        assertEquals(new BigDecimal("1425"), response.getSaldo());
        verify(movimientoRepository, times(1)).save(any(Movimiento.class));
    }
    
    @Test
    void registrarMovimiento_DeberiaLanzarExcepcionPorSaldoInsuficiente() {
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.findFirstByCuentaIdOrderByIdDesc(1L)).thenReturn(Optional.empty());
        
        movimientoRequest.setValor(new BigDecimal("-3000"));
        
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> movimientoService.registrarMovimiento(movimientoRequest)
        );
        
        assertEquals("Saldo no disponible", exception.getMessage());
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }
    
    @Test
    void registrarMovimiento_DeberiaLanzarExcepcionCuandoCuentaInactiva() {
        cuenta.setEstado(false);
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));
        
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> movimientoService.registrarMovimiento(movimientoRequest)
        );
        
        assertTrue(exception.getMessage().contains("inactiva"));
        verify(movimientoRepository, never()).save(any(Movimiento.class));
    }
}
