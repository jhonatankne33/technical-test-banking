package com.jvanegas.account_service.service;

import com.jvanegas.account_service.dto.MovimientoRequest;
import com.jvanegas.account_service.dto.MovimientoResponse;
import com.jvanegas.account_service.entity.Cuenta;
import com.jvanegas.account_service.entity.Movimiento;
import com.jvanegas.account_service.exception.BusinessException;
import com.jvanegas.account_service.exception.ResourceNotFoundException;
import com.jvanegas.account_service.repository.CuentaRepository;
import com.jvanegas.account_service.repository.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MovimientoService {
    
    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    
    public MovimientoService(MovimientoRepository movimientoRepository, CuentaRepository cuentaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
    }
    
    @Transactional
    public MovimientoResponse registrarMovimiento(MovimientoRequest request) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(request.getNumeroCuenta())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + request.getNumeroCuenta()));
        
        if (!cuenta.getEstado()) {
            throw new BusinessException("La cuenta está inactiva");
        }
        
        // Calculamos el nuevo saldo sumando el valor del movimiento al saldo actual.
        // Los valores negativos son retiros, los positivos son depósitos.
        BigDecimal saldoActual = calcularSaldoActual(cuenta);
        BigDecimal nuevoSaldo = saldoActual.add(request.getValor());
        
        // Si el saldo queda negativo, no permitimos la operación
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Saldo no disponible");
        }
        
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setValor(request.getValor());
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);
        
        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);
        return mapearAResponse(movimientoGuardado);
    }
    
    public List<MovimientoResponse> obtenerTodosLosMovimientos() {
        return movimientoRepository.findAll()
                .stream()
                .map(this::mapearAResponse).toList();
    }
    
    public MovimientoResponse obtenerMovimientoPorId(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
        return mapearAResponse(movimiento);
    }
    
    // No se permite actualizar movimientos porque si modificas uno anterior,
    // todos los saldos que vienen después quedan mal. Es mejor hacer un movimiento
    // de corrección en lugar de cambiar el historial.
    
    // Para calcular el saldo actual, simplemente tomamos el saldo del último movimiento.
    // Si no hay movimientos, usamos el saldo inicial de la cuenta.
    private BigDecimal calcularSaldoActual(Cuenta cuenta) {
        Optional<Movimiento> ultimoMovimiento = movimientoRepository.findFirstByCuentaIdOrderByIdDesc(cuenta.getId());
        
        if (ultimoMovimiento.isPresent()) {
            return ultimoMovimiento.get().getSaldo();
        } else {
            return cuenta.getSaldoInicial();
        
        }
    }
    
    private MovimientoResponse mapearAResponse(Movimiento movimiento) {
        MovimientoResponse response = new MovimientoResponse();
        response.setId(movimiento.getId());
        response.setFecha(movimiento.getFecha());
        response.setTipoMovimiento(movimiento.getTipoMovimiento());
        response.setValor(movimiento.getValor());
        response.setSaldo(movimiento.getSaldo());
        response.setNumeroCuenta(movimiento.getCuenta().getNumeroCuenta());
        return response;
    }
}
