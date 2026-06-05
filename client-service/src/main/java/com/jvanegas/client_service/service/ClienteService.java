package com.jvanegas.client_service.service;

import com.jvanegas.client_service.dto.ClienteRequest;
import com.jvanegas.client_service.dto.ClienteResponse;
import com.jvanegas.client_service.entity.Cliente;
import com.jvanegas.client_service.exception.BusinessException;
import com.jvanegas.client_service.exception.ResourceNotFoundException;
import com.jvanegas.client_service.messaging.ClienteEventPublisher;
import com.jvanegas.client_service.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    private final ClienteEventPublisher eventPublisher;
    
    public ClienteService(ClienteRepository clienteRepository, ClienteEventPublisher eventPublisher) {
        this.clienteRepository = clienteRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Transactional
    public ClienteResponse crearCliente(ClienteRequest request) {
        if (clienteRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new BusinessException("Ya existe un cliente con la identificación: " + request.getIdentificacion());
        }
        
        Cliente cliente = new Cliente();
        mapearDatosCliente(cliente, request);
        
        Cliente clienteGuardado = clienteRepository.save(cliente);
        return mapearAResponse(clienteGuardado);
    }
    
    public List<ClienteResponse> obtenerTodosLosClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(this::mapearAResponse).toList();
    }
    
    public ClienteResponse obtenerClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return mapearAResponse(cliente);
    }
    
    @Transactional
    public ClienteResponse actualizarCliente(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        
        if (!cliente.getIdentificacion().equals(request.getIdentificacion()) 
                && clienteRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new BusinessException("Ya existe un cliente con la identificación: " + request.getIdentificacion());
        }
        
        mapearDatosCliente(cliente, request);
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return mapearAResponse(clienteActualizado);
    }
    
    @Transactional
    public void eliminarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        
        clienteRepository.deleteById(id);
        
        eventPublisher.publicarClienteEliminado(cliente.getClienteId(), cliente.getIdentificacion());
    }
    
    private void mapearDatosCliente(Cliente cliente, ClienteRequest request) {
        cliente.setNombre(request.getNombre());
        cliente.setGenero(request.getGenero());
        cliente.setEdad(request.getEdad());
        cliente.setIdentificacion(request.getIdentificacion());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setContrasena(request.getContrasena());
        cliente.setEstado(request.getEstado());
    }
    
    private ClienteResponse mapearAResponse(Cliente cliente) {
        ClienteResponse response = new ClienteResponse();
        response.setClienteId(cliente.getClienteId());
        response.setNombre(cliente.getNombre());
        response.setGenero(cliente.getGenero());
        response.setEdad(cliente.getEdad());
        response.setIdentificacion(cliente.getIdentificacion());
        response.setDireccion(cliente.getDireccion());
        response.setTelefono(cliente.getTelefono());
        response.setEstado(cliente.getEstado());
        return response;
    }
}
