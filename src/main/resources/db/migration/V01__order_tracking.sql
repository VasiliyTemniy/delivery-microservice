CREATE TABLE IF NOT EXISTS delivery.order_trackings
(
    id                     BIGSERIAL PRIMARY KEY NOT NULL,
    order_id               BIGINT                NOT NULL,
    point_number           INTEGER               NOT NULL,
    from_facility_id       BIGINT                NOT NULL,
    destination_id         BIGINT                NOT NULL,
    destination_type       VARCHAR(60)           NOT NULL,
    carrier_id             BIGINT                NOT NULL,
    status                 VARCHAR(60)           NOT NULL,
    delivery_cost          INTEGER               NOT NULL,
    currency               VARCHAR(3)            NOT NULL,
    mass_control_value     INTEGER,
    mass_measure           VARCHAR(60),
    estimated_delivery_at  TIMESTAMP WITH TIME ZONE,
    delivered_at           TIMESTAMP WITH TIME ZONE,

    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP
);