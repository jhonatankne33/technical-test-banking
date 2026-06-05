package com.jvanegas.client_service.messaging;

import com.jvanegas.client_service.config.RabbitMQConfig;
import com.jvanegas.client_service.dto.ClienteEliminadoEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClienteEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public ClienteEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    // Cuando se elimina un cliente, publicamos un evento a RabbitMQ para que
    // el microservicio de cuentas desactive automáticamente todas sus cuentas.
    public void publicarClienteEliminado(Long clienteId, String identificacion) {
        ClienteEliminadoEvent event = new ClienteEliminadoEvent(clienteId, identificacion);
        rabbitTemplate.convertAndSend(RabbitMQConfig.CLIENTE_ELIMINADO_QUEUE, event);
    }
}
