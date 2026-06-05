package com.jvanegas.account_service.controller;

import com.jvanegas.account_service.dto.MovimientoRequest;
import com.jvanegas.account_service.dto.MovimientoResponse;
import com.jvanegas.account_service.service.MovimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {
    
    private final MovimientoService movimientoService;
    
    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }
    
    @PostMapping
    public ResponseEntity<MovimientoResponse> registrarMovimiento(@RequestBody MovimientoRequest request) {
        MovimientoResponse response = movimientoService.registrarMovimiento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> obtenerTodosLosMovimientos() {
        List<MovimientoResponse> movimientos = movimientoService.obtenerTodosLosMovimientos();
        return ResponseEntity.ok(movimientos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponse> obtenerMovimientoPorId(@PathVariable Long id) {
        MovimientoResponse response = movimientoService.obtenerMovimientoPorId(id);
        return ResponseEntity.ok(response);
    }
}
