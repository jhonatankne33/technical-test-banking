package com.jvanegas.account_service.repository;

import com.jvanegas.account_service.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    
    Optional<Movimiento> findFirstByCuentaIdOrderByIdDesc(Long cuentaId);
    
    List<Movimiento> findByCuentaClienteIdAndFechaBetweenOrderByFechaDesc(
            Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
