-- Tabela para configurações de autenticação multi-fator
CREATE TABLE mfa_configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    -- TOTP (Google Authenticator)
    totp_secret VARCHAR(255), -- Base32 encoded
    is_totp_enabled BOOLEAN DEFAULT FALSE,
    totp_verified_at TIMESTAMP,

    -- SMS OTP
    is_sms_enabled BOOLEAN DEFAULT FALSE,
    sms_phone_number VARCHAR(20),
    sms_verified_at TIMESTAMP,

    -- Biometria (preparação futura)
    is_biometric_enabled BOOLEAN DEFAULT FALSE,
    biometric_public_key TEXT,
    biometric_verified_at TIMESTAMP,

    -- Códigos de backup (array de hashes)
    backup_codes TEXT[], -- Array de códigos hasheados (bcrypt)
    backup_codes_generated_at TIMESTAMP,

    -- Metadados
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT unique_user_mfa UNIQUE (user_id),
    CONSTRAINT check_at_least_one_method CHECK (
        is_totp_enabled = TRUE OR
        is_sms_enabled = TRUE OR
        is_biometric_enabled = TRUE
    )
);

-- Índices para performance
CREATE INDEX idx_mfa_configs_user_id ON mfa_configs(user_id);
CREATE INDEX idx_mfa_configs_totp_enabled ON mfa_configs(is_totp_enabled) WHERE is_totp_enabled = TRUE;

-- Tabela para dispositivos confiáveis (skip 2FA por 30 dias)
CREATE TABLE trusted_devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    -- Identificação do dispositivo
    device_fingerprint VARCHAR(500) NOT NULL, -- Hash único do dispositivo
    device_name VARCHAR(255), -- Nome amigável: "iPhone 13 Pro"
    device_type VARCHAR(50), -- MOBILE, DESKTOP, TABLET

    -- Sistema operacional e navegador
    os VARCHAR(100),
    browser VARCHAR(100),

    -- Segurança
    ip_address VARCHAR(45), -- IPv4 ou IPv6
    user_agent TEXT,

    -- Geolocalização aproximada
    country_code CHAR(2),
    city VARCHAR(255),

    -- Status e validade
    is_active BOOLEAN DEFAULT TRUE,
    trusted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL '30 days'),

    -- Metadados
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT unique_user_device UNIQUE (user_id, device_fingerprint)
);

-- Índices
CREATE INDEX idx_trusted_devices_user_id ON trusted_devices(user_id);
CREATE INDEX idx_trusted_devices_fingerprint ON trusted_devices(device_fingerprint);
CREATE INDEX idx_trusted_devices_active ON trusted_devices(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_trusted_devices_expires_at ON trusted_devices(expires_at);

-- Tabela para histórico de verificações MFA
CREATE TABLE mfa_verifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    -- Tipo de verificação
    verification_type VARCHAR(50) NOT NULL, -- TOTP, SMS, BIOMETRIC, BACKUP_CODE

    -- Status
    success BOOLEAN NOT NULL,
    failure_reason VARCHAR(255),

    -- Contexto
    ip_address VARCHAR(45),
    user_agent TEXT,
    device_fingerprint VARCHAR(500),

    -- Timestamp
    verified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Metadados para auditoria
    metadata JSONB
);

-- Índices para auditoria e analytics
CREATE INDEX idx_mfa_verifications_user_id ON mfa_verifications(user_id, verified_at DESC);
CREATE INDEX idx_mfa_verifications_type ON mfa_verifications(verification_type);
CREATE INDEX idx_mfa_verifications_success ON mfa_verifications(success);
CREATE INDEX idx_mfa_verifications_verified_at ON mfa_verifications(verified_at DESC);

-- Trigger para atualizar updated_at
CREATE OR REPLACE FUNCTION update_mfa_configs_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_mfa_configs_updated_at
    BEFORE UPDATE ON mfa_configs
    FOR EACH ROW
    EXECUTE FUNCTION update_mfa_configs_updated_at();

-- Comentários para documentação
COMMENT ON TABLE mfa_configs IS 'Configurações de autenticação multi-fator dos usuários';
COMMENT ON TABLE trusted_devices IS 'Dispositivos confiáveis que podem pular 2FA por período determinado';
COMMENT ON TABLE mfa_verifications IS 'Histórico de tentativas de verificação MFA para auditoria';

COMMENT ON COLUMN mfa_configs.totp_secret IS 'Secret TOTP em Base32 para Google Authenticator';
COMMENT ON COLUMN mfa_configs.backup_codes IS 'Códigos de recuperação hasheados com bcrypt';
COMMENT ON COLUMN trusted_devices.device_fingerprint IS 'Hash único gerado no frontend baseado em características do dispositivo';
COMMENT ON COLUMN trusted_devices.expires_at IS 'Dispositivo precisa ser re-validado após expiração';

