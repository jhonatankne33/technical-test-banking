package com.jvanegas.account_service.controller;

import com.jvanegas.account_service.dto.CuentaRequest;
import com.jvanegas.account_service.dto.CuentaResponse;
import com.jvanegas.account_service.service.CuentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {
    
    private final CuentaService cuentaService;
    
    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }
    
    @PostMapping
    public ResponseEntity<CuentaResponse> crearCuenta(@RequestBody CuentaRequest request) {
        CuentaResponse response = cuentaService.crearCuenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<CuentaResponse>> obtenerTodasLasCuentas() {
        List<CuentaResponse> cuentas = cuentaService.obtenerTodasLasCuentas();
        return ResponseEntity.ok(cuentas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponse> obtenerCuentaPorId(@PathVariable Long id) {
        CuentaResponse response = cuentaService.obtenerCuentaPorId(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CuentaResponse> actualizarCuenta(
            @PathVariable Long id,
            @RequestBody CuentaRequest request) {
        CuentaResponse response = cuentaService.actualizarCuenta(id, request);
        return ResponseEntity.ok(response);
    }
}
