-- Script para postgres-client (clientdb)

-- Tabla clientes (incluye campos de Persona por herencia)
CREATE TABLE IF NOT EXISTS clientes (
    cliente_id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    genero VARCHAR(50) NOT NULL,
    edad INTEGER NOT NULL,
    identificacion VARCHAR(50) NOT NULL UNIQUE,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL
);

-- Datos de prueba para clientes
INSERT INTO clientes (nombre, genero, edad, identificacion, direccion, telefono, contrasena, estado)
VALUES 
('Jose Lema', 'Masculino', 35, '1234567890', 'Otavalo sn y principal', '098254785', '1234', true),
('Marianela Montalvo', 'Femenino', 28, '0987654321', 'Amazonas y NNUU', '097548965', '5678', true),
('Juan Osorio', 'Masculino', 42, '1122334455', '13 junio y Equinoccial', '098874587', '1245', true)
ON CONFLICT (identificacion) DO NOTHING;
