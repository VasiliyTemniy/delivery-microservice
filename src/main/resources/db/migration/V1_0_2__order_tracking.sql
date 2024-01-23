CREATE TABLE IF NOT EXISTS delivery.order_trackings
(
    id                     UUID                 DEFAULT uuid_generate_v4() PRIMARY KEY,
    order_id               BIGINT               NOT NULL,
    point_number           INTEGER              NOT NULL,
    from_facility_id       BIGINT               NOT NULL,
    destination_id         BIGINT               NOT NULL,
    destination_type       VARCHAR(60)          NOT NULL,
    carrier_id             BIGINT               NOT NULL,
    status                 VARCHAR(60)          NOT NULL,
    delivery_cost          INTEGER              NOT NULL,
    currency               VARCHAR(3)           NOT NULL,
    mass_control_value     INTEGER,
    mass_measure           VARCHAR(60),
    estimated_delivery_at  TIMESTAMP WITH TIME ZONE,
    delivered_at           TIMESTAMP WITH TIME ZONE,

    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_order_point_pair UNIQUE (order_id, point_number),
);

-- Set the default value for point_number based on the existing records for the same order_id
ALTER TABLE delivery.order_trackings
    ALTER COLUMN point_number SET DEFAULT (
        SELECT COALESCE(MAX(subquery.row_number), 0)
        FROM (
            SELECT ROW_NUMBER() OVER (PARTITION BY order_id ORDER BY id) AS row_number
            FROM delivery.order_trackings
        ) AS subquery
        WHERE subquery.order_id = order_trackings.order_id
    );

CREATE INDEX IF NOT EXISTS order_trackings_order_id_point_number_idx ON delivery.order_trackings (order_id, point_number);