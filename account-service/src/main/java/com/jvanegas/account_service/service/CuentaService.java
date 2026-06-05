package com.jvanegas.account_service.service;

import com.jvanegas.account_service.dto.CuentaRequest;
import com.jvanegas.account_service.dto.CuentaResponse;
import com.jvanegas.account_service.entity.Cuenta;
import com.jvanegas.account_service.exception.BusinessException;
import com.jvanegas.account_service.exception.ResourceNotFoundException;
import com.jvanegas.account_service.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final RestTemplate restTemplate;

    @Value("${client.service.url:http://localhost:8081}")
    private String clientServiceUrl;

    public CuentaService(CuentaRepository cuentaRepository, RestTemplate restTemplate) {
        this.cuentaRepository = cuentaRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public CuentaResponse crearCuenta(CuentaRequest request) {
        if (cuentaRepository.existsByNumeroCuenta(request.getNumeroCuenta())) {
            throw new BusinessException("Ya existe una cuenta con el número: " + request.getNumeroCuenta());
        }

        // Validamos que el cliente exista en el otro microservicio antes de crear la cuenta.
        // Esto evita crear cuentas de clientes que no existen.
        validarClienteExiste(request.getClienteId());

        Cuenta cuenta = new Cuenta();
        mapearDatosCuenta(cuenta, request);

        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        return mapearAResponse(cuentaGuardada);
    }

    public List<CuentaResponse> obtenerTodasLasCuentas() {
        return cuentaRepository.findAll()
                .stream()
                .map(this::mapearAResponse).toList();
    }

    public CuentaResponse obtenerCuentaPorId(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));
        return mapearAResponse(cuenta);
    }

    public CuentaResponse obtenerCuentaPorNumero(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + numeroCuenta));
        return mapearAResponse(cuenta);
    }

    @Transactional
    public CuentaResponse actualizarCuenta(Long id, CuentaRequest request) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));

        if (!cuenta.getNumeroCuenta().equals(request.getNumeroCuenta())
                && cuentaRepository.existsByNumeroCuenta(request.getNumeroCuenta())) {
            throw new BusinessException("Ya existe una cuenta con el número: " + request.getNumeroCuenta());
        }

        mapearDatosCuenta(cuenta, request);
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        return mapearAResponse(cuentaActualizada);
    }

    private void validarClienteExiste(Long clienteId) {
        try {
            String url = clientServiceUrl + "/clientes/" + clienteId;
            restTemplate.getForObject(url, Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + clienteId);
        } catch (Exception e) {
            throw new BusinessException("Error al validar cliente con id: " + clienteId);
        }
    }

    private void mapearDatosCuenta(Cuenta cuenta, CuentaRequest request) {
        cuenta.setNumeroCuenta(request.getNumeroCuenta());
        cuenta.setTipoCuenta(request.getTipoCuenta());
        cuenta.setSaldoInicial(request.getSaldoInicial());
        cuenta.setEstado(request.getEstado());
        cuenta.setClienteId(request.getClienteId());
    }

    private CuentaResponse mapearAResponse(Cuenta cuenta) {
        CuentaResponse response = new CuentaResponse();
        response.setId(cuenta.getId());
        response.setNumeroCuenta(cuenta.getNumeroCuenta());
        response.setTipoCuenta(cuenta.getTipoCuenta());
        response.setSaldoInicial(cuenta.getSaldoInicial());
        response.setEstado(cuenta.getEstado());
        response.setClienteId(cuenta.getClienteId());
        return response;
    }
}
