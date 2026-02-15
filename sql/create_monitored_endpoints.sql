CREATE TABLE IF NOT EXISTS monitored_endpoints (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    url             TEXT        NOT NULL,
    frequency       VARCHAR(20) NOT NULL CHECK (frequency IN ('DAILY', 'HOURLY', 'EVERY_15_MINUTES')),
    expected_status_code INTEGER NOT NULL,
    expected_response    TEXT,
    email           VARCHAR(255) NOT NULL,
    user_id         VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP   NOT NULL DEFAULT now()
);
