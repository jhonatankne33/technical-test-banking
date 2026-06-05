package com.jvanegas.client_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEliminadoEvent implements Serializable {
    
    private Long clienteId;
    private String identificacion;
}
