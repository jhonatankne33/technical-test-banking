package com.jvanegas.account_service.messaging;

import com.jvanegas.account_service.config.RabbitMQConfig;
import com.jvanegas.account_service.dto.ClienteEliminadoEvent;
import com.jvanegas.account_service.entity.Cuenta;
import com.jvanegas.account_service.repository.CuentaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ClienteEventConsumer {
    
    private final CuentaRepository cuentaRepository;
    
    public ClienteEventConsumer(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }
    
    // Escuchamos eventos de RabbitMQ. Cuando se elimina un cliente,
    // automáticamente desactivamos todas sus cuentas.
    @RabbitListener(queues = RabbitMQConfig.CLIENTE_ELIMINADO_QUEUE)
    @Transactional
    public void procesarClienteEliminado(ClienteEliminadoEvent event) {
        List<Cuenta> cuentas = cuentaRepository.findByClienteId(event.getClienteId());
        
        for (Cuenta cuenta : cuentas) {
            cuenta.setEstado(false);
            cuentaRepository.save(cuenta);
        }
    }
}
