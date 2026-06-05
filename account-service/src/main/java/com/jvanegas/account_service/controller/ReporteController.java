package com.jvanegas.account_service.controller;

import com.jvanegas.account_service.dto.EstadoCuentaResponse;
import com.jvanegas.account_service.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reportes")
public class ReporteController {
    
    private final ReporteService reporteService;
    
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }
    
    @GetMapping
    public ResponseEntity<EstadoCuentaResponse> generarEstadoCuenta(
            @RequestParam Long cliente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        EstadoCuentaResponse response = reporteService.generarEstadoCuenta(cliente, fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }
}
