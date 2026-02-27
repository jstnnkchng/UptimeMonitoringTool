CREATE TABLE IF NOT EXISTS check_results (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    endpoint_id     BIGINT      NOT NULL REFERENCES monitored_endpoints(id),
    status_code     INTEGER,
    response_body   TEXT,
    passed          BOOLEAN     NOT NULL,
    failure_reason  TEXT,
    checked_at      TIMESTAMP   NOT NULL DEFAULT now()
);