package com.jvanegas.account_service.repository;

import com.jvanegas.account_service.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    
    List<Cuenta> findByClienteId(Long clienteId);
    
    boolean existsByNumeroCuenta(String numeroCuenta);
}
