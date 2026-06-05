package com.jvanegas.account_service.service;

import com.jvanegas.account_service.dto.EstadoCuentaResponse;
import com.jvanegas.account_service.entity.Cuenta;
import com.jvanegas.account_service.entity.Movimiento;
import com.jvanegas.account_service.repository.CuentaRepository;
import com.jvanegas.account_service.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final RestTemplate restTemplate;

    @Value("${client.service.url:http://localhost:8081}")
    private String clientServiceUrl;

    public ReporteService(CuentaRepository cuentaRepository, MovimientoRepository movimientoRepository,
            RestTemplate restTemplate) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
        this.restTemplate = restTemplate;
    }

    public EstadoCuentaResponse generarEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        String nombreCliente = obtenerNombreCliente(clienteId);

        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);

        LocalDateTime fechaInicioDateTime = fechaInicio.atStartOfDay();
        LocalDateTime fechaFinDateTime = fechaFin.atTime(LocalTime.MAX);

        List<Movimiento> movimientos = movimientoRepository
                .findByCuentaClienteIdAndFechaBetweenOrderByFechaDesc(clienteId, fechaInicioDateTime, fechaFinDateTime);

        EstadoCuentaResponse response = new EstadoCuentaResponse();
        response.setCliente(nombreCliente);
        response.setCuentas(new ArrayList<>());

        for (Cuenta cuenta : cuentas) {
            EstadoCuentaResponse.CuentaDetalle cuentaDetalle = new EstadoCuentaResponse.CuentaDetalle();
            cuentaDetalle.setNumeroCuenta(cuenta.getNumeroCuenta());
            cuentaDetalle.setTipoCuenta(cuenta.getTipoCuenta());
            cuentaDetalle.setSaldoInicial(cuenta.getSaldoInicial());
            cuentaDetalle.setEstado(cuenta.getEstado());
            cuentaDetalle.setMovimientos(new ArrayList<>());

            List<Movimiento> movimientosCuenta = movimientos.stream()
                    .filter(m -> m.getCuenta().getId().equals(cuenta.getId()))
                    .toList();

            for (Movimiento movimiento : movimientosCuenta) {
                EstadoCuentaResponse.MovimientoDetalle movimientoDetalle = new EstadoCuentaResponse.MovimientoDetalle();
                movimientoDetalle.setFecha(movimiento.getFecha());
                movimientoDetalle.setTipoMovimiento(movimiento.getTipoMovimiento());
                movimientoDetalle.setValor(movimiento.getValor());
                movimientoDetalle.setSaldoDisponible(movimiento.getSaldo());
                cuentaDetalle.getMovimientos().add(movimientoDetalle);
            }

            if (!movimientosCuenta.isEmpty()) {
                cuentaDetalle.setSaldoDisponible(movimientosCuenta.get(0).getSaldo());
            } else {
                cuentaDetalle.setSaldoDisponible(cuenta.getSaldoInicial());
            }

            response.getCuentas().add(cuentaDetalle);
        }

        return response;
    }

    @SuppressWarnings("unchecked")
    private String obtenerNombreCliente(Long clienteId) {
        try {
            String url = clientServiceUrl + "/clientes/" + clienteId;
            Map<String, Object> clienteData = restTemplate.getForObject(url, Map.class);
            if (clienteData != null && clienteData.containsKey("nombre")) {
                return (String) clienteData.get("nombre");
            }
        } catch (HttpClientErrorException.NotFound e) {
            return "Cliente no encontrado";
        } catch (Exception e) {
            return "Cliente ID: " + clienteId;
        }
        return "Cliente ID: " + clienteId;
    }
}
