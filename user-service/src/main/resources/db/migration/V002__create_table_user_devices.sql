CREATE TABLE user_devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    user_agent TEXT NOT NULL,
    CONSTRAINT fk_user_device_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
