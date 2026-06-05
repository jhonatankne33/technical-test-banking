package com.jvanegas.client_service.service;

import com.jvanegas.client_service.dto.ClienteRequest;
import com.jvanegas.client_service.dto.ClienteResponse;
import com.jvanegas.client_service.entity.Cliente;
import com.jvanegas.client_service.exception.BusinessException;
import com.jvanegas.client_service.exception.ResourceNotFoundException;
import com.jvanegas.client_service.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    
    @Mock
    private ClienteRepository clienteRepository;
    
    @InjectMocks
    private ClienteService clienteService;
    
    private ClienteRequest clienteRequest;
    private Cliente cliente;
    
    @BeforeEach
    void setUp() {
        clienteRequest = new ClienteRequest();
        clienteRequest.setNombre("Jose Lema");
        clienteRequest.setGenero("Masculino");
        clienteRequest.setEdad(35);
        clienteRequest.setIdentificacion("1234567890");
        clienteRequest.setDireccion("Otavalo sn y principal");
        clienteRequest.setTelefono("098254785");
        clienteRequest.setContrasena("1234");
        clienteRequest.setEstado(true);
        
        cliente = new Cliente();
        cliente.setClienteId(1L);
        cliente.setNombre("Jose Lema");
        cliente.setGenero("Masculino");
        cliente.setEdad(35);
        cliente.setIdentificacion("1234567890");
        cliente.setDireccion("Otavalo sn y principal");
        cliente.setTelefono("098254785");
        cliente.setContrasena("1234");
        cliente.setEstado(true);
    }
    
    @Test
    void crearCliente_DeberiaCrearClienteExitosamente() {
        when(clienteRepository.existsByIdentificacion(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        
        ClienteResponse response = clienteService.crearCliente(clienteRequest);
        
        assertNotNull(response);
        assertEquals("Jose Lema", response.getNombre());
        assertEquals("1234567890", response.getIdentificacion());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }
    
    @Test
    void crearCliente_DeberiaLanzarExcepcionCuandoIdentificacionExiste() {
        when(clienteRepository.existsByIdentificacion(anyString())).thenReturn(true);
        
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> clienteService.crearCliente(clienteRequest)
        );
        
        assertTrue(exception.getMessage().contains("Ya existe un cliente"));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
    
    @Test
    void obtenerClientePorId_DeberiaRetornarCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        
        ClienteResponse response = clienteService.obtenerClientePorId(1L);
        
        assertNotNull(response);
        assertEquals(1L, response.getClienteId());
        assertEquals("Jose Lema", response.getNombre());
    }
    
    @Test
    void obtenerClientePorId_DeberiaLanzarExcepcionCuandoNoExiste() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(
                ResourceNotFoundException.class,
                () -> clienteService.obtenerClientePorId(999L)
        );
    }
    
    @Test
    void actualizarCliente_DeberiaActualizarExitosamente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        
        clienteRequest.setNombre("Jose Lema Actualizado");
        ClienteResponse response = clienteService.actualizarCliente(1L, clienteRequest);
        
        assertNotNull(response);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }
    
    @Test
    void eliminarCliente_DeberiaEliminarExitosamente() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        
        clienteService.eliminarCliente(1L);
        
        verify(clienteRepository, times(1)).deleteById(1L);
    }
}
