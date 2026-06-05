-- Script para postgres-account (accountdb)

-- Tabla cuentas
CREATE TABLE IF NOT EXISTS cuentas (
    id BIGSERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(50) NOT NULL UNIQUE,
    tipo_cuenta VARCHAR(50) NOT NULL,
    saldo_inicial DECIMAL(19,2) NOT NULL,
    estado BOOLEAN NOT NULL,
    cliente_id BIGINT NOT NULL
);

-- Tabla movimientos
CREATE TABLE IF NOT EXISTS movimientos (
    id BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL,
    tipo_movimiento VARCHAR(50) NOT NULL,
    valor DECIMAL(19,2) NOT NULL,
    saldo DECIMAL(19,2) NOT NULL,
    cuenta_id BIGINT NOT NULL,
    CONSTRAINT fk_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuentas(id)
);

-- Datos de prueba para cuentas
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id)
VALUES 
('478758', 'Ahorros', 2000.00, true, 1),
('225487', 'Corriente', 100.00, true, 2),
('495878', 'Ahorros', 0.00, true, 3),
('496825', 'Ahorros', 540.00, true, 2),
('585545', 'Corriente', 1000.00, true, 1)
ON CONFLICT (numero_cuenta) DO NOTHING;
