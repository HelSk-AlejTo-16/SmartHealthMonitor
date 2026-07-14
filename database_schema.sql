-- Tabla principal de lecturas de frecuencia cardíaca
CREATE TABLE IF NOT EXISTS lecturas_fc (
    id           SERIAL PRIMARY KEY,
    bpm          INTEGER NOT NULL CHECK (bpm > 0 AND bpm < 300),
    estado       VARCHAR(20) NOT NULL DEFAULT 'Normal',
    dispositivo  VARCHAR(10) NOT NULL DEFAULT 'wear',  -- wear | app | tv
    hora         VARCHAR(10) NOT NULL,
    fecha        DATE NOT NULL DEFAULT CURRENT_DATE,
    sincronizado BOOLEAN DEFAULT FALSE,  -- TRUE = ya está en Room local
    created_at   TIMESTAMPTZ DEFAULT NOW()
);

-- Tabla de alertas
CREATE TABLE IF NOT EXISTS alertas (
    id          SERIAL PRIMARY KEY,
    tipo        VARCHAR(30) NOT NULL,  -- FC_ALTA | FC_BAJA | FC_NORMAL
    bpm         INTEGER NOT NULL,
    mensaje     TEXT NOT NULL,
    atendida    BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

-- Tabla de dispositivos registrados
CREATE TABLE IF NOT EXISTS dispositivos (
    id          SERIAL PRIMARY KEY,
    tipo        VARCHAR(10) UNIQUE NOT NULL,  -- wear | app | tv
    ultimo_sync TIMESTAMPTZ,
    activo      BOOLEAN DEFAULT TRUE
);

-- Índices para consultas frecuentes
CREATE INDEX idx_lecturas_fecha ON lecturas_fc(fecha DESC);
CREATE INDEX idx_lecturas_dispositivo ON lecturas_fc(dispositivo);
CREATE INDEX idx_alertas_atendida ON alertas(atendida);
